package com.samsepiol.file.nexus.storage.config.source;

import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import com.samsepiol.file.nexus.storage.config.FileFilterConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for source configurations.
 */
@Data
@NoArgsConstructor
public abstract class AbstractSourceConfig {
    /**
     * The name of the storage.
     */
    private String name;

    /**
     * The type of the storage S3, GCS, SFTP
     */
    private StorageType type;

    /**
     * The list of destination configurations.
     */
    private List<Map<String, Object>> destinations = new ArrayList<>();

    /**
     * File filtering configuration.
     */
    private FileFilterConfig fileFiltering;

    /**
     * Schedule configuration for the source.
     * This is used to determine if the source is scheduled or not.
     */
    private String schedule;

    /**
     * Flag to indicate if the source is enabled.
     * Defaults to true.
     */
    private Boolean enabled = false;

    /**
     * Optional prefix for the source.
     */
    private String prefix;

    /**
     * Constructor with all fields.
     *
     * @param name          The name of the storage
     * @param type          The type of the storage
     * @param destinations  The list of destination configurations
     * @param fileFiltering File filtering configuration
     * @param schedule      The schedule configuration for the source
     */
    public AbstractSourceConfig(String name, StorageType type, List<Map<String, Object>> destinations,
                                FileFilterConfig fileFiltering, String schedule, Boolean enabled) {
        this.name = name;
        this.type = type;
        this.destinations = destinations;
        this.fileFiltering = fileFiltering;
        this.schedule = schedule;
        this.enabled = enabled;
    }

    /**
     * Constructor without schedule.
     *
     * @param name          The name of the storage
     * @param type          The type of the storage
     * @param destinations  The list of destination configurations
     * @param fileFiltering File filtering configuration
     */
    public AbstractSourceConfig(String name, StorageType type, List<Map<String, Object>> destinations,
                                FileFilterConfig fileFiltering) {
        this.name = name;
        this.type = type;
        this.destinations = destinations;
        this.fileFiltering = fileFiltering;
    }

    /**
     * Check if this source is scheduled (not ASAP).
     *
     * @return true if the source has a schedule, false otherwise
     */
    public boolean isScheduled() {
        return schedule != null && !schedule.trim().isEmpty();
    }
}
