package com.samsepiol.file.nexus.metadata.parser.type.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileSuffixParserRequest {
    @NonNull
    String fileType;

    @NonNull
    String fileName;
}
