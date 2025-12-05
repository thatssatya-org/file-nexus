package com.samsepiol.file.nexus.storage.util;

import com.samsepiol.file.nexus.storage.config.FileFilterConfig;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for filtering files based on configuration.
 */
@Slf4j
public class FileFilterUtil {

    /**
     * Filter files based on the provided configuration.
     *
     * @param files             The list of files to filter
     * @param config            The filter configuration
     * @param lastProcessedTime The last processed timestamp
     * @return The filtered list of files
     */
    public static List<FileInfo> filterFiles(List<FileInfo> files, FileFilterConfig config, Instant lastProcessedTime) {
        if (files == null || files.isEmpty()) {
            return files;
        }

        return files.stream()
                .filter(file -> shouldProcessFile(file, config, lastProcessedTime))
                .collect(Collectors.toList());
    }

    /**
     * Determine if a file should be processed based on the configuration.
     *
     * @param file              The file to check
     * @param config            The filter configuration
     * @param lastProcessedTime The last processed timestamp
     * @return true if the file should be processed, false otherwise
     */
    private static boolean shouldProcessFile(FileInfo file, FileFilterConfig config, Instant lastProcessedTime) {

        if (file.getSize() == 0) {
            log.debug("Skipping file {} due to zero size", file.getFilePath());
            return false;
        }

        if (config.getMinFileAge() != null && file.getLastModified() != null) {
            Instant minAllowedTime = Instant.now().minus(config.getMinFileAge());
            if (file.getLastModified().isAfter(minAllowedTime)) {
                log.debug("Skipping file {} due to minimum age requirement", file.getFilePath());
                return false;
            }
        }

        if (config.isTimestampFilteringEnabled() && lastProcessedTime != null && file.getLastModified() != null) {
            if (!file.getLastModified().isAfter(lastProcessedTime)) {
                log.debug("Skipping file {} due to timestamp filtering", file.getFilePath());
                return false;
            }
        }

        return true;
    }

    /**
     * Check if a file path matches a pattern.
     * Supports simple glob patterns with * and ?.
     *
     * @param filePath The file path to check
     * @param pattern  The pattern to match against
     * @return true if the file path matches the pattern
     */
    private static boolean matchesPattern(String filePath, String pattern) {
        if (pattern == null || filePath == null) {
            return false;
        }

        try {
            // Convert glob pattern to regex
            String regexPattern = pattern
                    .replace(".", "\\.")
                    .replace("*", ".*")
                    .replace("?", ".");

            Pattern compiledPattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);

            // Check both full path and just the filename
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            return compiledPattern.matcher(filePath).matches() ||
                    compiledPattern.matcher(fileName).matches();
        } catch (Exception e) {
            log.warn("Error matching pattern {} against file {}: {}", pattern, filePath, e.getMessage());
            return false;
        }
    }
}
