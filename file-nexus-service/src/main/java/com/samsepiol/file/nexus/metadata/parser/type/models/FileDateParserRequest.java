package com.samsepiol.file.nexus.metadata.parser.type.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileDateParserRequest {
    @NonNull
    String fileName;

    @NonNull
    String fileType;
}
