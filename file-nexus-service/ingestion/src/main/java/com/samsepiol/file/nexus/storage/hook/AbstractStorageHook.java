package com.samsepiol.file.nexus.storage.hook;

import com.samsepiol.file.nexus.enums.FileStateStatus;
import com.samsepiol.file.nexus.storage.config.FileFilterConfig;
import com.samsepiol.file.nexus.storage.config.HookConfigurationProvider;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import com.samsepiol.file.nexus.storage.models.PollResult;
import com.samsepiol.file.nexus.storage.service.FileStateService;
import com.samsepiol.file.nexus.storage.service.TimestampTrackingService;
import com.samsepiol.file.nexus.storage.util.FileFilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.samsepiol.file.nexus.enums.FileStateStatus.PROCESSED;
import static com.samsepiol.file.nexus.enums.FileStateStatus.SCHEDULED;

/**
 * Abstract base class for storage hooks that implements common functionality.
 * Provides locking and file state tracking.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractStorageHook implements StorageHook {

    private static final String STATE_PREFIX = "nexus:file-state:";

    protected final TimestampTrackingService timestampTrackingService;
    protected final FileStateService fileStateService;
    private final HookConfigurationProvider hookConfigurationProvider;

    /**
     * List files from the storage with pagination and filtering support.
     * This method should be implemented by subclasses to retrieve files from their specific storage.
     *
     * @param lastProcessedTime Only return files modified after this time
     * @param maxResults        Maximum number of files to return
     * @param prefix
     * @return A list of file information from the storage
     */
    protected abstract List<FileInfo> listFiles(Instant lastProcessedTime, int maxResults, String prefix);


    @Override
    public PollResult poll() {
        try {
            FileFilterConfig filterConfig = getFilterConfig();
            if (filterConfig == null) {
                filterConfig = new FileFilterConfig();
            }

            Instant lastProcessedTime = null;
            if (filterConfig.isTimestampFilteringEnabled()) {
                lastProcessedTime = timestampTrackingService.getLastProcessedTimestampOrDefault(
                        getName(),
                        filterConfig.getTimestampKeyPrefix(),
                        Instant.now().minus(Duration.ofDays(1))
                );
            }

            List<FileInfo> allFiles = listFiles(lastProcessedTime, filterConfig.getPageSize(), getPrefix());

            List<FileInfo> filteredFiles = FileFilterUtil.filterFiles(allFiles, filterConfig, lastProcessedTime);

            List<FileInfo> newFiles = new ArrayList<>();
            Instant latestTimestamp = lastProcessedTime;

            for (FileInfo fileInfo : filteredFiles) {
                String filePath = fileInfo.getFilePath();
                FileStateStatus fileState = getFileState(filePath);

                if (SCHEDULED.equals(fileState) || FileStateStatus.PROCESSED.equals(fileState)) {
                    continue;
                }
                newFiles.add(fileInfo);
                if (fileInfo.getLastModified() != null &&
                        (latestTimestamp == null || fileInfo.getLastModified().isAfter(latestTimestamp))) {
                    latestTimestamp = fileInfo.getLastModified();
                }
            }

            log.debug("Polled {} files, filtered to {} files, processing {} new files",
                    allFiles.size(), filteredFiles.size(), newFiles.size());

            return new PollResult(newFiles, filterConfig.isTimestampFilteringEnabled() && latestTimestamp != null ?
                    latestTimestamp : Instant.now());
        } catch (Exception e) {
            log.error("Error during polling for hook {}: {}", getName(), e.getMessage(), e);
            return new PollResult(new ArrayList<>(), Instant.now());
        }
    }

    @Override
    public void setLastProcessedTimestamp(Instant timestamp) {
        FileFilterConfig filterConfig = getFilterConfig();
        if (filterConfig != null && filterConfig.isTimestampFilteringEnabled()) {
            timestampTrackingService.updateLastProcessedTimestamp(
                    getName(),
                    filterConfig.getTimestampKeyPrefix(),
                    timestamp
            );
        } else {
            log.warn("Timestamp filtering is not enabled for hook: {}", getName());
        }
    }

    @Override
    public void markScheduled(String filePath) {
        setFileState(filePath, SCHEDULED);
    }

    @Override
    public void markProcessed(String filePath) {
        setFileState(filePath, PROCESSED);
    }

    /**
     * Get the state of a file.
     *
     * @param filePath The path of the file
     * @return The state of the file, or null if the file has no state
     */
    protected FileStateStatus getFileState(String filePath) {
        return fileStateService.getFileState(getStateKey(filePath));
    }

    /**
     * Set the state of a file.
     *
     * @param filePath The path of the file
     * @param state    The state to set
     */
    protected void setFileState(String filePath, FileStateStatus state) {
        String stateKey = getStateKey(filePath);
        fileStateService.setFileState(filePath, getName(), stateKey, state);
    }

    /**
     * Get the state key for a file.
     *
     * @param filePath The path of the file
     * @return The state key
     */
    protected String getStateKey(String filePath) {
        return STATE_PREFIX + getName() + ":" + filePath;
    }

    /**
     * Get the filter configuration for this hook.
     *
     * @return The filter configuration, or null if not found
     */
    protected FileFilterConfig getFilterConfig() {
        return hookConfigurationProvider.getProcessedConfiguration()
                .getSourceByName(getName()).getSourceConfig().getFileFiltering();
    }

    protected String getPrefix() {
        return hookConfigurationProvider.getProcessedConfiguration()
                .getSourceByName(getName()).getSourceConfig().getPrefix();
    }
}
