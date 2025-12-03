package com.samsepiol.file.nexus.storage.destination.models;

import com.samsepiol.file.nexus.storage.config.destination.AbstractDestinationConfig;
import com.samsepiol.file.nexus.storage.hook.StorageHook;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendFileRequestDto {

    private StorageHook storageHook;
    private String fileName;
    private String sourceName;
    private AbstractDestinationConfig config;
    private FileInfo fileInfo;

}
