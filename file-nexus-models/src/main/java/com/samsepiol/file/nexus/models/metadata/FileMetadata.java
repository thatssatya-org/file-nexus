package com.samsepiol.file.nexus.models.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.samsepiol.file.nexus.models.enums.MetadataStatus;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileMetadata {

    @NonNull
    String id;

    @NonNull
    String fileType;

    @NonNull
    String date;

    @NonNull
    String fileName;

    @NonNull
    MetadataStatus status;

}
