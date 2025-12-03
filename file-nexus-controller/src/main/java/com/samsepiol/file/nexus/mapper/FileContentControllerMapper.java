package com.samsepiol.file.nexus.mapper;

import com.samsepiol.file.nexus.content.data.models.FileContent;
import com.samsepiol.file.nexus.content.data.models.response.FileContents;
import com.samsepiol.file.nexus.content.models.request.FileContentFetchServiceRequest;
import com.samsepiol.file.nexus.models.content.request.FileContentsQuery;
import com.samsepiol.file.nexus.models.content.response.FileContentsResponse;
import com.samsepiol.file.nexus.models.content.response.dto.FileContentResponse;
import com.samsepiol.file.nexus.models.content.response.dto.FileMetadataResponse;
import com.samsepiol.helper.utils.CommonSerializationUtil;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FileContentControllerMapper {

    FileContentControllerMapper MAPPER = Mappers.getMapper(FileContentControllerMapper.class);

    @Mapping(target = "date", source = "date")
    FileContentFetchServiceRequest toFileContentFetchServiceRequest(String fileType,
                                                                    LocalDate date,
                                                                    FileContentsQuery query);

    default @NonNull FileContentsResponse toFileContentsResponse(String fileType, FileContentsQuery query, FileContents fileContents) {
        return FileContentsResponse.builder()
                .fileType(fileType)
                .date(query.getDate())
                .contents(toFileContents(fileContents))
                .metadata(FileMetadataResponse.completed())
                .build();
    }

    default List<FileContentResponse> toFileContents(FileContents fileContents) {
        return fileContents.getContents().stream()
                .map(this::toFileContentsResponse)
                .toList();
    }

    default FileContentResponse toFileContentsResponse(FileContent fileContent) {
        return FileContentResponse.builder()
                .rowReferenceId(fileContent.getId())
                .fileId(fileContent.getFileId())
                .rowNumber(fileContent.getRowNumber())
                .content(CommonSerializationUtil.readMapFromString(fileContent.getContent()))
                .build();
    }

}
