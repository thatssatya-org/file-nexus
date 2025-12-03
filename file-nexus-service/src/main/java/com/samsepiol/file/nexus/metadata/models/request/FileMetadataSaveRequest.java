package com.samsepiol.file.nexus.metadata.models.request;


import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileMetadataSaveRequest {
    @NonNull
    ParsedFileMetaData parsedFileMetaData;
    @NonNull
    MetadataStatus status;

    public static FileMetadataSaveRequest forCompleted(@NonNull ParsedFileMetaData parsedFileMetaData) {
        return FileMetadataSaveRequest.builder()
                .parsedFileMetaData(parsedFileMetaData)
                .status(MetadataStatus.COMPLETED)
                .build();
    }

    public static FileMetadataSaveRequest forPending(@NonNull ParsedFileMetaData parsedFileMetaData) {
        return FileMetadataSaveRequest.builder()
                .parsedFileMetaData(parsedFileMetaData)
                .status(MetadataStatus.PENDING)
                .build();
    }
}
