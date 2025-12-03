package com.samsepiol.file.nexus.content.config;

import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import lombok.Data;

@Data
public class StoreConfigEntry {
    private String identifier;
    private StorageType storageType;
    private String host;
    private String privateKey;
    private String passphrase;
    private String aliasName;
    private Integer port;
    private String username;
    private String rootDir;
}
