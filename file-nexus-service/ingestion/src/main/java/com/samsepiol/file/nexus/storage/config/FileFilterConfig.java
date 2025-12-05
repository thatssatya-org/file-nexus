package com.samsepiol.file.nexus.storage.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * Configuration for file filtering in storage hooks.
 */
@Data
@NoArgsConstructor
public class FileFilterConfig {

    /**
     * Minimum age of files to process. Files newer than this will be skipped.
     * This helps avoid processing files that are still being written.
     */
    private Duration minFileAge = Duration.ofMinutes(5);

    /**
     * Maximum number of files to return in a single listFiles call.
     */
    private int pageSize = 100;

    /**
     * Whether to enable timestamp-based filtering.
     */
    private boolean timestampFilteringEnabled = true;

    /**
     * Key prefix for storing last processed timestamps in Redis.
     */
    private String timestampKeyPrefix = "nexus:last-processed:";
}
