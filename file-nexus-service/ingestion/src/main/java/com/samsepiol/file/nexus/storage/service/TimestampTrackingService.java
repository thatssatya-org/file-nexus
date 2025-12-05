package com.samsepiol.file.nexus.storage.service;

import com.samsepiol.library.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service for tracking last processed timestamps.
 * This helps maintain state across pod restarts and ensures
 * only new files are processed.
 * <p>
 * Note: Currently using in-memory storage. For production use,
 * this should be backed by Redis or a database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimestampTrackingService {
    private final Cache<String, Long> cache;

    /**
     * Get the last processed timestamp for a storage hook.
     *
     * @param hookName  The name of the storage hook
     * @param keyPrefix The key prefix
     * @return The last processed timestamp, or null if not found
     */
    public Instant getLastProcessedTimestamp(String hookName, String keyPrefix) {
        try {
            String key = keyPrefix + hookName;
            Long timestampStr = cache.get(key);
            if (timestampStr == null) {
                log.debug("No last processed timestamp found for hook: {}", hookName);
                return null;
            }
            return Instant.ofEpochMilli(timestampStr);
        } catch (Exception e) {
            log.warn("Failed to get last processed timestamp for hook: {}", hookName, e);
        }

        return null;
    }

    /**
     * Update the last processed timestamp for a storage hook.
     *
     * @param hookName  The name of the storage hook
     * @param keyPrefix The key prefix
     * @param timestamp The timestamp to store
     */
    public void updateLastProcessedTimestamp(String hookName, String keyPrefix, Instant timestamp) {
        try {
            String key = keyPrefix + hookName;
            cache.put(key, timestamp.toEpochMilli());
            log.debug("Updated last processed timestamp for hook {}: {}", hookName, timestamp);
        } catch (Exception e) {
            log.error("Failed to update last processed timestamp for hook: {}", hookName, e);
        }
    }

    /**
     * Get the last processed timestamp or return a default value.
     *
     * @param hookName         The name of the storage hook
     * @param keyPrefix        The key prefix
     * @param defaultTimestamp The default timestamp to return if none exists
     * @return The last processed timestamp or the default value
     */
    public Instant getLastProcessedTimestampOrDefault(String hookName, String keyPrefix, Instant defaultTimestamp) {
        Instant lastProcessed = getLastProcessedTimestamp(hookName, keyPrefix);
        return lastProcessed != null ? lastProcessed : defaultTimestamp;
    }
}
