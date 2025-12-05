package com.samsepiol.file.nexus.ingestion.workflow.activities.impl;


import com.samsepiol.file.nexus.ingestion.workflow.activities.ISendToDestinationActivity;
import com.samsepiol.file.nexus.ingestion.workflow.dto.FileIngestionWorkflowRequest;
import com.samsepiol.file.nexus.storage.config.HookConfigurationProvider;
import com.samsepiol.file.nexus.storage.config.destination.AbstractDestinationConfig;
import com.samsepiol.file.nexus.storage.destination.DestinationHandler;
import com.samsepiol.file.nexus.storage.destination.models.SendFileRequestDto;
import com.samsepiol.file.nexus.storage.hook.StorageHook;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import com.samsepiol.file.nexus.storage.service.StorageHookMonitoringService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component

@RequiredArgsConstructor
public class SendToDestinationActivity implements ISendToDestinationActivity {

    private final List<DestinationHandler> destinationHandlers;
    private final HookConfigurationProvider hookConfigurationProvider;
    
    private final StorageHookMonitoringService storageHookMonitoringService;
    
    private static final String METRIC_PREFIX = "file.nexus.scheduled.source.destination.";


    private static final String FILE_PROCESSED_EVENT_NAME = "FILE_NEXUS_FILE_PROCESSED";
    private static final String FILE_PROCESSING_FAILED_EVENT_NAME = "FILE_NEXUS_FILE_PROCESSING_FAILED";
    private static final String DESTINATION_SEND_ATTEMPT_EVENT = "FILE_NEXUS_DESTINATION_SEND_ATTEMPT";


    @Override
    public boolean sendToDestinationWithConfig(String source,
                                               FileInfo fileInfo, String destination) {
        final String filePath = fileInfo.getFilePath();
        final String fileName = extractFileName(filePath);
        
        AbstractDestinationConfig destinationConfig = hookConfigurationProvider.getProcessedConfiguration()
            .getSourceByName(source).getDestinationByName(destination);
        log.info("Sending file: {} to destination type: {}", filePath, destinationConfig.getType());

        emitSendAttemptEvent(source, fileName, filePath, fileInfo.getSize(), destination,
                           destinationConfig.getType().name(), System.currentTimeMillis());

        DestinationHandler handler = findDestinationHandler(destinationConfig);
        if (handler == null) {
            log.error("No handler found for destination type: {}", destinationConfig.getType());
            return false;
        }

        boolean success = false;

        try {
            Optional<StorageHook> storageHookOptional = storageHookMonitoringService.getStorageHook(source);
            if (storageHookOptional.isEmpty()) {
                log.error("No storage hook found for source: {}", source);
                emmitMetric("send.error", source, destinationConfig);
                return false;
            }

            StorageHook storageHook = storageHookOptional.get();

            SendFileRequestDto sendFileRequestDto = SendFileRequestDto.builder()
                    .storageHook(storageHook)
                    .fileName(fileName)
                    .sourceName(source)
                    .config(destinationConfig)
                    .fileInfo(fileInfo)
                    .build();

            success = handler.sendFile(sendFileRequestDto);

            if (success) {
                log.info("Successfully sent file: {} to destination type: {}", filePath, destinationConfig.getType());
                emmitMetric("send.success", source, destinationConfig);
                storageHook.markProcessed(filePath);
            } else {
                log.error("Failed to send file: {} to destination type: {}", filePath, destinationConfig.getType());
                emmitMetric("send.failure", source, destinationConfig);
                // TODOeventHelper.publishSync(FILE_PROCESSING_FAILED_EVENT_NAME,
//                    new FileEvent(source, fileName, List.of(destinationConfig.getName())));
            }
        } catch (Exception e) {
            log.error("Error sending file: {} to destination type: {}", filePath, destinationConfig.getType(), e);
            emmitMetric("send.error", source, destinationConfig);
        }

        return success;
    }

    @Override
    public void markFileAsProcessed(FileIngestionWorkflowRequest request) {
        String source = request.getSourceName();
        String filePath = request.getFilePath();
        try {
            StorageHook storageHook = storageHookMonitoringService.getStorageHook(source)
                .orElseThrow(() -> new RuntimeException("No storage hook found for source: " + source));
            storageHook.markProcessed(filePath);
            log.info("Marked file {} as processed for source {}", filePath, source);
            // TODOeventHelper.publishSync(FILE_PROCESSED_EVENT_NAME,
//                new FileEvent(source, extractFileName(filePath), request.getDestinations()));
        } catch (Exception e) {
            log.error("Failed to mark file {} as processed for source {}", filePath, source, e);
        }
    }

    @Override
    public void markFileAsScheduled(FileIngestionWorkflowRequest request) {
        String source = request.getSourceName();
        String filePath = request.getFilePath();
        try {
            StorageHook storageHook = storageHookMonitoringService.getStorageHook(source)
                .orElseThrow(() -> new RuntimeException("No storage hook found for source: " + source));
            storageHook.markScheduled(filePath);
            log.info("Marked file {} as scheduled for source {}", filePath, source);
        } catch (Exception e) {
            log.error("Failed to mark file {} as scheduled for source {}", filePath, source, e);
        }
    }

    private void emmitMetric(String metricName, String source, AbstractDestinationConfig destinationConfig) {
//        metricHelper.incrementCounter(METRIC_PREFIX + metricName,
//                Map.of("source", source, "destination", destinationConfig.getType().name()));
    }

    /**
     * Find the appropriate destination handler for the given destination configuration.
     * 
     * @param config The destination configuration
     * @return The destination handler, or null if no handler is found
     */
    private DestinationHandler findDestinationHandler(AbstractDestinationConfig config) {
        return destinationHandlers.stream()
            .filter(handler -> handler.canHandle(config.getType()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Extract the file name from a path.
     *
     * @param filePath The full path of the file
     * @return The file name without a path
     */
    private String extractFileName(String filePath) {
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }

    /**
     * Emit destination send attempt event
     */
    private void emitSendAttemptEvent(String sourceName, String fileName, String filePath, 
                                    long fileSize, String destinationName, String destinationType, 
                                    long timestamp) {
        try {
            DestinationSendAttemptEvent event = new DestinationSendAttemptEvent(
                sourceName, fileName, filePath, fileSize, destinationName, destinationType, timestamp
            );
            // TODOeventHelper.publishSync(DESTINATION_SEND_ATTEMPT_EVENT, event);
        } catch (Exception e) {
            log.error("Failed to emit send attempt event for file: {}, destination: {}", 
                     fileName, destinationName, e);
        }
    }


    private record FileEvent(String source, String fileName, List<String> destinationType) {}
    
    private record DestinationSendAttemptEvent(String sourceName, String fileName, String filePath, 
                                             long fileSize, String destinationName, String destinationType, 
                                             long timestamp) {}
}
