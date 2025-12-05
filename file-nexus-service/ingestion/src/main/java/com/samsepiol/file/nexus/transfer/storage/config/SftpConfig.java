package com.samsepiol.file.nexus.transfer.storage.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SftpConfig {

    private String host;
    private String privateKey;
    private String passphrase;
    private String aliasName;
    private Integer port;
    private String username;
    private String rootDir;

}
