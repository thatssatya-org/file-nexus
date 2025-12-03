package com.samsepiol.file.nexus.storage.hook;

import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import com.samsepiol.file.nexus.storage.models.PollResult;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

/**
 * Interface for storage hooks that monitor storage backends for new files.
 * Implementations should handle polling, locking, and state tracking.
 */
public interface StorageHook {

    void initializeWithBucketNameAndHookName(String bucketName, String hookName);

    /**
     * Poll the storage for new files.
     * This method should be called periodically to check for new files.
     * It should use Redis locking to prevent concurrent processing of the same file.
     *
     * @return A list of file paths that were detected and locked for processing
     */
    PollResult poll();

    void setLastProcessedTimestamp(Instant timestamp);

    void markScheduled(String filePath);

    /**
     * Mark a file as processed.
     * This method should be called after a file has been successfully processed.
     *
     * @param filePath The path of the file to mark as processed
     */
    void markProcessed(String filePath);

    /**
     * Get the name of the storage hook.
     * This should be a unique identifier for the hook.
     *
     * @return The name of the storage hook
     */
    String getName();

    /**
     * Get the content of a file as an InputStream.
     * This method should be preferred for large files to enable streaming.
     * Implementations are responsible for ensuring the stream is valid and can be read.
     * The caller is responsible for closing the stream.
     *
     * @param fileKey The key or path of the file within the bucket.
     * @return An InputStream to read the file content.
     * @throws java.io.IOException If an I/O error occurs when opening the stream.
     */
    InputStream getFileAsStream(String fileKey, long offsetByte) throws IOException;

    StorageType getStorageType();
}
