package com.samsepiol.file.nexus.transfer.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Builder
@Configuration
@ConfigurationProperties(prefix = "file-transfer-config")
public class FileTransferConfig {
    private List<FileTransferConfigEntry> fileTransferConfigs;
}
