package com.samsepiol.file.nexus.storage.service;

import com.samsepiol.file.nexus.storage.config.source.AbstractSourceConfig;
import com.samsepiol.file.nexus.transfer.storage.CloseableInputStream;
import com.samsepiol.file.nexus.transfer.storage.client.IStorageClient;

/**
 * Service interface for file storage operations.
 * This service abstracts the storage client operations and provides a clean interface
 * for file operations across different storage types (S3, GCP, SFTP).
 */
public interface FileStorageService {

    /**
     * Downloads a file from the specified source configuration.
     *
     * @param sourceConfig The source storage configuration
     * @param filePath     The path of the file to download
     * @return CloseableInputStream containing the file data
     * @throws IllegalArgumentException if the source configuration is invalid
     * @throws RuntimeException         if the file download fails
     */
    CloseableInputStream downloadFile(AbstractSourceConfig sourceConfig, String filePath);

    /**
     * Gets a storage client for the given configuration.
     *
     * @param config The storage configuration
     * @return IStorageClient instance for the configuration
     * @throws IllegalArgumentException if the configuration is invalid
     */
    IStorageClient getStorageClient(AbstractSourceConfig config);

    /**
     * Extracts the bucket/container name from the source configuration.
     * This method handles the logic for determining the appropriate bucket/container
     * name based on the storage type and configuration.
     *
     * @param sourceConfig The source storage configuration
     * @return The bucket/container name
     */
    String getBucketName(AbstractSourceConfig sourceConfig);
}
