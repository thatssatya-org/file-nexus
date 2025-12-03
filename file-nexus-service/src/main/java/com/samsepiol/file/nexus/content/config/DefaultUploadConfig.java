package com.samsepiol.file.nexus.content.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultUploadConfig {

    private String bucketName;
    private String targetPath;
    private String archiveDir;

}