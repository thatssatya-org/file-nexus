package com.samsepiol.file.nexus.content.data.parser.models.request;

import com.samsepiol.file.nexus.content.data.models.enums.FileContentType;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileContentParsingRequest {
    @NonNull
    String fileType;
    @NonNull
    Object content;

    @NonNull
    @Builder.Default
    FileContentType contentType = FileContentType.PLAIN_STRING;

    public static FileContentParsingRequest plainString(@NonNull String fileType, @NonNull String content) {
        return FileContentParsingRequest.builder().fileType(fileType).content(content).build();
    }

}
