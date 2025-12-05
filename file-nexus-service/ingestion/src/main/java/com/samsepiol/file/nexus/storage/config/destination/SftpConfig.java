package com.samsepiol.file.nexus.storage.config.destination;

import com.samsepiol.file.nexus.storage.destination.DestinationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Configuration for SFTP destination.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SftpConfig extends AbstractDestinationConfig {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String privateKey;
    private String passphrase;
    private String remoteDirectory;
    private Boolean strictHostKeyChecking;

    @Override
    public DestinationType getType() {
        return DestinationType.SFTP;
    }
}
