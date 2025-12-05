package com.samsepiol.file.nexus.storage.config.source;

import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import com.samsepiol.file.nexus.storage.config.FileFilterConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Configuration for SFTP storage sources.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SftpSourceConfig extends AbstractSourceConfig {
    /**
     * The host name or IP address.
     */
    private String host;

    /**
     * The private key for authentication.
     */
    private String privateKey;

    /**
     * The passphrase for the private key.
     */
    private String passphrase;

    /**
     * The alias name.
     */
    private String aliasName;

    /**
     * The port number.
     */
    private Integer port;

    /**
     * The username for authentication.
     */
    private String username;

    /**
     * The root directory.
     */
    private String rootDir;

    /**
     * Constructor with all fields including scheduledTime.
     *
     * @param name          The name of the storage
     * @param type          The type of the storage (SFTP)
     * @param destinations  The list of destination configurations
     * @param fileFiltering File filtering configuration
     * @param scheduledTime The time at which the file should be processed (HH:MM)
     * @param host          The host name or IP address
     * @param privateKey    The private key for authentication
     * @param passphrase    The passphrase for the private key
     * @param aliasName     The alias name
     * @param port          The port number
     * @param username      The username for authentication
     * @param rootDir       The root directory
     */
    public SftpSourceConfig(String name, StorageType type, List<Map<String, Object>> destinations,
                            FileFilterConfig fileFiltering, String scheduledTime, String host, String privateKey,
                            String passphrase, String aliasName, Integer port, String username, String rootDir, Boolean enabled) {
        super(name, type, destinations, fileFiltering, scheduledTime, enabled);
        this.host = host;
        this.privateKey = privateKey;
        this.passphrase = passphrase;
        this.aliasName = aliasName;
        this.port = port;
        this.username = username;
        this.rootDir = rootDir;
    }

    /**
     * Constructor without scheduledTime.
     *
     * @param name          The name of the storage
     * @param type          The type of the storage (SFTP)
     * @param destinations  The list of destination configurations
     * @param fileFiltering File filtering configuration
     * @param host          The host name or IP address
     * @param privateKey    The private key for authentication
     * @param passphrase    The passphrase for the private key
     * @param aliasName     The alias name
     * @param port          The port number
     * @param username      The username for authentication
     * @param rootDir       The root directory
     */
    public SftpSourceConfig(String name, StorageType type, List<Map<String, Object>> destinations,
                            FileFilterConfig fileFiltering, String host, String privateKey, String passphrase,
                            String aliasName, Integer port, String username, String rootDir) {
        super(name, type, destinations, fileFiltering);
        this.host = host;
        this.privateKey = privateKey;
        this.passphrase = passphrase;
        this.aliasName = aliasName;
        this.port = port;
        this.username = username;
        this.rootDir = rootDir;
    }
}