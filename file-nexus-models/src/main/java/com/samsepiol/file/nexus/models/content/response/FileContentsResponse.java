package com.samsepiol.file.nexus.models.content.response;

import com.samsepiol.file.nexus.models.content.response.dto.FileContentResponse;
import com.samsepiol.file.nexus.models.content.response.dto.FileMetadataResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileContentsResponse {
    String fileType;
    String date;
    FileMetadataResponse metadata;
    @Builder.Default
    List<FileContentResponse> contents = Collections.emptyList();

    public static FileContentsResponse discarded(String fileType, String date) {
        return FileContentsResponse.builder().fileType(fileType).date(date)
                .metadata(FileMetadataResponse.discarded())
                .build();
    }

    public static FileContentsResponse pending(String fileType, String date) {
        return FileContentsResponse.builder().fileType(fileType).date(date)
                .metadata(FileMetadataResponse.pending())
                .build();
    }
}
