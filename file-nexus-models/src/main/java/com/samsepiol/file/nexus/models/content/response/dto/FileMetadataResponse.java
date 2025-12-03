package com.samsepiol.file.nexus.models.content.response.dto;

import com.samsepiol.file.nexus.models.content.response.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileMetadataResponse {
    Status ingestionStatus;

    public static FileMetadataResponse discarded() {
        return FileMetadataResponse.builder().ingestionStatus(Status.DISCARDED).build();
    }

    public static FileMetadataResponse pending() {
        return FileMetadataResponse.builder().ingestionStatus(Status.PENDING).build();
    }

    public static FileMetadataResponse completed() {
        return FileMetadataResponse.builder().ingestionStatus(Status.COMPLETED).build();
    }
}
