package com.samsepiol.file.nexus.content.config;

import com.samsepiol.file.nexus.transfer.config.RenameFileConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadConfig {

    private String fileNameRegex;
    private String archiveDir;
    private String uploadBucketName;
    private String uploadBucketPath;
    private RenameFileConfig renameFileConfig;

}