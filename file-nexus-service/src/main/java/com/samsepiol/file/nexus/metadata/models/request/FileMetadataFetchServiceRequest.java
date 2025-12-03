package com.samsepiol.file.nexus.metadata.models.request;


import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileMetadataFetchServiceRequest {

    @NonNull
    String fileType;

    @NonNull
    String date;
}
