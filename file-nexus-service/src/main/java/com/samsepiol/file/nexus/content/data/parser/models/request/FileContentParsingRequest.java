package com.samsepiol.file.nexus.content.data.parser.models.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class FileContentParsingRequest {
    @NonNull
    String fileType;
    @NonNull
    String content;

    public static FileContentParsingRequest of(@NonNull String fileType, @NonNull String content) {
        return FileContentParsingRequest.builder().fileType(fileType).content(content).build();
    }
}
