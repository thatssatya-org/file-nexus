package com.samsepiol.file.nexus.transfer.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileTransferRequestConfig {
    private String filePath;
    private String archiveDir;
    private String bucketName;
    private String bucketPath;
    private RenameFileConfig renameFileConfig;
}
