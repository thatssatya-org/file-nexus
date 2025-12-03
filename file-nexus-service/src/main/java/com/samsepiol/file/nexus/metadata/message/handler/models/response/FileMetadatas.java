package com.samsepiol.file.nexus.metadata.message.handler.models.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public class FileMetadatas {
    @NonNull
    List<FileMetadata> metadatas;

    public boolean isEmpty() {
        return metadatas.isEmpty();
    }

    public @NonNull List<String> getFileIdsWithCompletedStatus() {
        return metadatas.stream()
                .filter(FileMetadata::isStatusCompleted)
                .map(FileMetadata::getId)
                .toList();
    }

    public static @NonNull FileMetadatas from(@NonNull List<FileMetadata> metadatas) {
        return FileMetadatas.builder().metadatas(metadatas).build();
    }

    public boolean isIngestionPending() {
        return metadatas.stream().anyMatch(FileMetadata::isStatusPending);
    }

    public boolean isDiscarded() {
        return metadatas.stream().allMatch(FileMetadata::isStatusDiscarded);
    }
}
