package com.samsepiol.file.nexus.content.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class FileContentSaveServiceRequest {

    @NonNull
    String fileId;

    @NonNull
    String fileType;

    @NonNull
    String fileName;

    @NonNull
    @Builder.Default
    List<String> fileContents = Collections.emptyList();

    public static @NonNull FileContentSaveServiceRequest from(String fileId,
                                                              String fileType,
                                                              String fileContent) {

        return FileContentSaveServiceRequest.builder()
                .fileId(fileId)
                .fileType(fileType)
                .fileContents(List.of(fileContent))
                .build();
    }

}
