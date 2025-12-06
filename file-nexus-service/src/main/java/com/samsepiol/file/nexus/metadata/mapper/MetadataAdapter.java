package com.samsepiol.file.nexus.metadata.mapper;

import com.samsepiol.file.nexus.metadata.models.response.FileMetadata;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MetadataAdapter {

    public static FileMetadata fileMetadataFromEntity(MetadataEntity metadata){
        return FileMetadata.builder()
                .id(metadata.getId())
                .date(metadata.getDate())
                .fileType(metadata.getFileType())
                .status(metadata.getStatus())
                .fileName(metadata.getFileName())
                .build();
    }

    public static List<FileMetadata> fileMetadataListFromEntityList(List<MetadataEntity> list){
         return list.stream()
                 .map(MetadataAdapter::fileMetadataFromEntity)
                 .toList();
    }
}
