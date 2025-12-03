package com.samsepiol.file.nexus.repo.content.models.request;

import com.samsepiol.file.nexus.repo.content.entity.FileContent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class FileContentSaveRepositoryRequest {
    @NonNull
    List<FileContent> entities;

    public static @NonNull FileContentSaveRepositoryRequest from(@NonNull List<FileContent> entities) {
        return FileContentSaveRepositoryRequest.builder()
                .entities(entities)
                .build();
    }

    public static @NonNull FileContentSaveRepositoryRequest from(@NonNull FileContent entity) {
        return FileContentSaveRepositoryRequest.builder()
                .entities(Collections.singletonList(entity))
                .build();
    }
}
