package com.samsepiol.file.nexus.content.mapper;

import com.samsepiol.file.nexus.content.data.models.request.FileContentFetchRequest;
import com.samsepiol.file.nexus.content.data.models.request.FileContentSaveRequest;
import com.samsepiol.file.nexus.content.models.request.FileContentFetchServiceRequest;
import com.samsepiol.file.nexus.content.models.request.FileContentSaveServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface FileContentServiceMapper {

    FileContentServiceMapper MAPPER = Mappers.getMapper(FileContentServiceMapper.class);

    default FileContentSaveRequest toFileContentSaveRequest(FileContentSaveServiceRequest request,
                                                            List<Map<String, Object>> parsedFileContents) {
        return FileContentSaveRequest.builder()
                .fileId(request.getFileId())
                .fileType(request.getFileType())
                .fileContents(parsedFileContents)
                .build();
    }

    default FileContentFetchRequest toFileContentFetchRequest(FileContentFetchServiceRequest request,
                                                              List<String> fileIds) {
        return FileContentFetchRequest.builder()
                .fileType(request.getFileType())
                .fileIds(fileIds)
                .filters(request.getFilters())
                .build();
    }
}
