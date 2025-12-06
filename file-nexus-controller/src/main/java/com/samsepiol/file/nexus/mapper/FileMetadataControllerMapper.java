package com.samsepiol.file.nexus.mapper;

import com.samsepiol.file.nexus.models.metadata.FileMetadata;
import com.samsepiol.file.nexus.models.metadata.FileMetadatas;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileMetadataControllerMapper {

    FileMetadataControllerMapper MAPPER = Mappers.getMapper(FileMetadataControllerMapper.class);

    FileMetadatas from(com.samsepiol.file.nexus.metadata.models.response.FileMetadatas data);

    FileMetadata from(com.samsepiol.file.nexus.metadata.models.response.FileMetadata metadata);
}
