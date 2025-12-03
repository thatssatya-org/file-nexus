package com.samsepiol.file.nexus.content.config;

import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileTransferConfigEntry {
    private String identifier;
    private String srcRootDir;
    private Boolean extractAndProcessZipEntries;
    private StorageType sourceStorageType;
    private StorageType targetStorageType;
    private DefaultUploadConfig defaultUploadConfig;
    private List<FileUploadConfig> fileUploadConfig;
}
