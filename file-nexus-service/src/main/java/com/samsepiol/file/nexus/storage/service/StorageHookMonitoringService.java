package com.samsepiol.file.nexus.storage.service;

import com.samsepiol.file.nexus.ingestion.workflow.IFileIngestionWorkflow;
import com.samsepiol.file.nexus.ingestion.workflow.dto.FileIngestionWorkflowRequest;
import com.samsepiol.file.nexus.lock.ExecuteWithLockService;
import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import com.samsepiol.file.nexus.storage.config.HookConfigurationProvider;
import com.samsepiol.file.nexus.storage.config.ProcessedHookConfiguration;
import com.samsepiol.file.nexus.storage.config.ProcessedSource;
import com.samsepiol.file.nexus.storage.config.destination.AbstractDestinationConfig;
import com.samsepiol.file.nexus.storage.config.source.AbstractSourceConfig;
import com.samsepiol.file.nexus.storage.config.source.S3GcsSourceConfig;
import com.samsepiol.file.nexus.storage.hook.StorageHook;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import com.samsepiol.file.nexus.storage.models.PollResult;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.samsepiol.file.nexus.ingestion.workflow.utils.WorkflowOptionsRegistry.getIFileIngestionWorkflowOptions;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageHookMonitoringService {

    private final WorkflowClient workflowClient;
    private final ExecuteWithLockService lockService;
    private final HookConfigurationProvider hookConfigurationProvider;
    private final HookFactory hookFactory;
    private final ScheduleManagementService scheduleManagementService;
    private final List<StorageHook> realtimeStorageHooks = new java.util.ArrayList<>();
    private final List<StorageHook> scheduledStorageHooks = new java.util.ArrayList<>();
    private final Map<String, StorageHook> scheduledHooksByName = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<String, StorageHook> realtimeHooksByName = new java.util.concurrent.ConcurrentHashMap<>();
    private ProcessedHookConfiguration processedConfiguration;

    @PostConstruct
    public void initialize() {
        log.info("Initializing StorageHookMonitoringService");
        this.processedConfiguration = hookConfigurationProvider.getProcessedConfiguration();
        initializeStorageHooks();
        log.info("Initialized {} realtime hooks and {} scheduled hooks",
                realtimeStorageHooks.size(), scheduledStorageHooks.size());
    }

    private void initializeStorageHooks() {
        hookConfigurationProvider.getProcessedConfiguration()
                .getSources()
                .stream().filter(a -> Boolean.TRUE.equals(a.getSourceConfig().getEnabled()))
                .forEach(
                        processedSource -> {
                            StorageHook storageHook = createHookForSource(processedSource);
                            if (isScheduledSource(processedSource)) {
                                log.info("Initializing scheduled source {} - adding to scheduled polling",
                                        getSourceName(processedSource));

                                if (storageHook != null) {
                                    scheduledStorageHooks.add(storageHook);
                                    scheduledHooksByName.put(getSourceName(processedSource), storageHook);
                                    scheduleManagementService.createOrUpdateSchedule(processedSource.getSourceConfig());
                                }
                                return;
                            }

                            if (storageHook != null) {
                                log.info("Initializing realtime source {} - adding to ASAP polling",
                                        getSourceName(processedSource));
                                realtimeStorageHooks.add(storageHook);
                                realtimeHooksByName.put(getSourceName(processedSource), storageHook);
                            }
                        }
                );

        hookConfigurationProvider.getProcessedConfiguration()
                .getSources()
                .stream().filter(a -> Boolean.FALSE.equals(a.getSourceConfig().getEnabled()))
                .forEach(processedSource -> {
                    if (isScheduledSource(processedSource)) {
                        log.info("Scheduled source {} is disabled, pausing schedule",
                                getSourceName(processedSource));
                        scheduleManagementService.pauseSchedule(processedSource.getSourceConfig());
                    }
                });
    }

    private StorageHook createHookForSource(ProcessedSource processedSource) {
        StorageType type = processedSource.getSourceConfig().getType();
        StorageHook hook = hookFactory.getHook(type);

        switch (type) {
            case S3, GCS:
                S3GcsSourceConfig sourceConfig = (S3GcsSourceConfig) processedSource.getSourceConfig();
                hook.initializeWithBucketNameAndHookName(
                        sourceConfig.getBucket(),
                        getSourceName(processedSource));
                break;
            default:
                log.warn("Unsupported storage type: {}", type);
                return null;
        }
        return hook;
    }

    @Scheduled(fixedRateString = "${nexus.storage-hooks.polling-interval-ms:10000}")
    public void pollStorageHooks() {
        log.debug("Polling storage hooks for new files");

        try {
            lockService.execute(() -> {
                try {
                    pollRealtimeHooks();
                } catch (Exception e) {
                    log.debug("Error polling storage hooks", e);
                }
            }, "storage-hook-monitoring");
            //TODO
//        } catch (ParallelLockException e) {
//            log.debug("Storage hook monitoring is already in progress, skipping this poll", e);
        } catch (Exception e) {
            log.error("Unexpected error during storage hook polling", e);
        }
    }

    private void pollRealtimeHooks() {
        for (StorageHook hook : realtimeStorageHooks) {
            try {
                log.info("Polling hook: {}", hook.getName());
                PollResult pollResult = hook.poll();
                if (!pollResult.files().isEmpty()) {
                    log.info("New files detected by hook {}: {}", hook.getName(), pollResult.files().size());
                    processNewFiles(hook.getName(), pollResult, false);
                } else {
                    log.debug("No new files found for hook: {}", hook.getName());
                }
            } catch (Exception e) {
                log.error("Error polling hook: {}", hook.getName(), e);
            }
        }
    }

    public List<String> processNewFiles(String hookName, PollResult pollResult, boolean isScheduled) {
        StorageHook hook = isScheduled ? scheduledHooksByName.get(hookName) : realtimeHooksByName.get(hookName);
        if (hook == null) {
            log.error("Hook not found: {}", hookName);
            return List.of();
        }
        List<String> workflowIds = new ArrayList<>();
        for (FileInfo fileInfo : pollResult.files()) {
            try {
                log.info("Processing file: {} from hook: {}", fileInfo.getFilePath(), hookName);
                String workflowId = startWorkflow(hook, fileInfo);
                if (workflowId != null) {
                    log.info("Started workflow: {} for file: {}", workflowId, fileInfo.getFilePath());
                    workflowIds.add(workflowId);
                } else {
                    log.warn("No enabled destinations found for hook: {}, skipping file: {}", hookName, fileInfo.getFilePath());
                }
            } catch (Exception e) {
                log.error("Error processing file: {} from hook: {}", fileInfo.getFilePath(), hookName, e);
            }
        }
        hook.setLastProcessedTimestamp(pollResult.latestTimestamp());
        log.info("Processed {} files from hook: {}", pollResult.files().size(), hookName);
        return workflowIds;
    }

    private String startWorkflow(StorageHook hook, FileInfo fileInfo) {
        String hookName = hook.getName();
        List<String> enabledDestinations = processedConfiguration.getSourceByName(hookName)
                .getEnabledDestinations()
                .stream()
                .map(AbstractDestinationConfig::getName)
                .collect(Collectors.toList());
        String filePath = fileInfo.getFilePath();
        if (enabledDestinations.isEmpty()) {
            log.debug("No enabled destinations found for hook: {}", hookName);
            return null;
        }

        FileIngestionWorkflowRequest request = FileIngestionWorkflowRequest.builder()
                .sourceName(hookName)
                .filePath(filePath)
                .destinations(enabledDestinations)
                .fileInfo(fileInfo)
                .build();

        String workflowId = generateWorkflowId(hookName, fileInfo,
                isScheduledSource(processedConfiguration.getSourceByName(hookName)));

        log.info("Starting workflow for file: {} from hook: {} with {} destinations",
                filePath, hookName, enabledDestinations.size());
        try {
            IFileIngestionWorkflow workflow = workflowClient.newWorkflowStub(IFileIngestionWorkflow.class,
                    getIFileIngestionWorkflowOptions(workflowId));
            WorkflowClient.start(workflow::processFile, request);
        } catch (WorkflowExecutionAlreadyStarted e) {
            log.warn("Workflow already started for file: {} from hook: {}, reusing existing workflow",
                    filePath, hookName);
            return null; // Return null to indicate no new workflow was started
        } catch (Exception e) {
            log.error("Error starting workflow for file: {} from hook: {}", filePath, hookName, e);
            return null;
        }
        return workflowId;
    }

    private boolean isScheduledSource(ProcessedSource processedSource) {
        try {
            AbstractSourceConfig sourceConfig = processedSource.getSourceConfig();
            if (sourceConfig == null) {
                return false;
            }
            return sourceConfig.isScheduled();
        } catch (Exception e) {
            log.warn("Error checking if source is scheduled, defaulting to ASAP mode", e);
            return false;
        }
    }

    private String getSourceName(ProcessedSource processedSource) {
        try {
            if (processedSource.getSourceConfig() instanceof S3GcsSourceConfig) {
                return processedSource.getSourceConfig().getName();
            }
            return "unknown-source";
        } catch (Exception e) {
            log.warn("Error getting source name, using default", e);
            return "unknown-source";
        }
    }

    public Optional<PollResult> pollScheduledSource(String sourceName) {
        log.info("Polling scheduled source: {}", sourceName);
        StorageHook hook = scheduledHooksByName.get(sourceName);
        if (hook == null) {
            log.error("Scheduled hook not found: {}", sourceName);
            return Optional.empty();
        }

        try {
            PollResult pollResult = hook.poll();
            log.info("Found {} files for scheduled source: {}", pollResult.files().size(), sourceName);
            return Optional.of(pollResult);
        } catch (Exception e) {
            log.error("Error polling scheduled source: {}", sourceName, e);
            return Optional.empty();
        }
    }

    public String generateWorkflowId(String sourceName, FileInfo fileInfo, boolean isScheduled) {
        String prefix = isScheduled ? "scheduled-file-ingestion" : "file-ingestion";
        return String.format("%s-%s-%s",
                prefix,
                sourceName,
                fileInfo.getFileKey().replace('/', '-').replace(" ", "_"));
    }

    public Optional<StorageHook> getStorageHook(String source) {
        if (realtimeHooksByName.containsKey(source)) {
            return Optional.of(realtimeHooksByName.get(source));
        } else if (scheduledHooksByName.containsKey(source)) {
            return Optional.of(scheduledHooksByName.get(source));
        } else {
            log.warn("No storage hook found for source: {}", source);
            return Optional.empty();
        }
    }
}
