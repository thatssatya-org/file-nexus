package com.samsepiol.file.nexus.storage.config.source;

import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import com.samsepiol.file.nexus.storage.config.FileFilterConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Configuration for S3 and GCP storage sources.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class S3GcsSourceConfig extends AbstractSourceConfig {
    /**
     * The bucket name.
     */
    private String bucket;

    /**
     * Constructor with all fields including scheduledTime.
     *
     * @param name          The name of the storage
     * @param type          The type of the storage (S3 or GCP)
     * @param destinations  The list of destination configurations
     * @param fileFiltering File filtering configuration
     * @param scheduledTime The time at which the file should be processed (HH:MM)
     * @param bucket        The bucket name
     */
    public S3GcsSourceConfig(String name, StorageType type, List<Map<String, Object>> destinations,
                             FileFilterConfig fileFiltering, String scheduledTime, String bucket, Boolean enabled) {
        super(name, type, destinations, fileFiltering, scheduledTime, enabled);
        this.bucket = bucket;
    }

    /**
     * Constructor without scheduledTime.
     *
     * @param name          The name of the storage
     * @param type          The type of the storage (S3 or GCP)
     * @param destinations  The list of destination configurations
     * @param fileFiltering File filtering configuration
     * @param bucket        The bucket name
     */
    public S3GcsSourceConfig(String name, StorageType type, List<Map<String, Object>> destinations,
                             FileFilterConfig fileFiltering, String bucket) {
        super(name, type, destinations, fileFiltering);
        this.bucket = bucket;
    }
}