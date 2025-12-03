package com.samsepiol.file.nexus.metadata.parser.models.response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class ParsedFileMetaData {
    @NonNull
    String fileId;
    @NonNull
    String name;
    @NonNull
    String fileType;
    @NonNull
    String fileDate;
    Long createdAt;
}
