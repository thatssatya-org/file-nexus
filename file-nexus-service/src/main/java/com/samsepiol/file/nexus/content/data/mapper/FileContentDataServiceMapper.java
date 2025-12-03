package com.samsepiol.file.nexus.content.data.mapper;

import com.samsepiol.file.nexus.content.data.models.FileContent;
import com.samsepiol.file.nexus.content.data.models.response.FileContents;
import com.samsepiol.file.nexus.repo.content.models.request.FileContentFetchRepositoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface FileContentDataServiceMapper {

    FileContentDataServiceMapper MAPPER = Mappers.getMapper(FileContentDataServiceMapper.class);

    default FileContentFetchRepositoryRequest from(List<String> fileIds, Map<String, String> query) {
        return FileContentFetchRepositoryRequest.builder()
                .fileIds(fileIds)
                .query(query)
                .build();
    }

    default FileContents from(List<com.samsepiol.file.nexus.repo.content.entity.FileContent> fileContents) {
        return FileContents.builder()
                .contents(prepareFileContents(fileContents))
                .build();
    }

    default List<FileContent> prepareFileContents(List<com.samsepiol.file.nexus.repo.content.entity.FileContent> fileContents) {
        return fileContents.stream()
                .map(this::from)
                .toList();
    }

    FileContent from(com.samsepiol.file.nexus.repo.content.entity.FileContent fileContent);
}
