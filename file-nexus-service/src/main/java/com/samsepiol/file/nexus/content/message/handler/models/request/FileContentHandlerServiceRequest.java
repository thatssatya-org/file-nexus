package com.samsepiol.file.nexus.content.message.handler.models.request;

import com.samsepiol.file.nexus.content.data.models.enums.FileContentType;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class FileContentHandlerServiceRequest {

    @NonNull
    Object message;

    @NonNull
    @Builder.Default
    FileContentType fileContentType = FileContentType.PLAIN_STRING;

    @NonNull
    Map<String, String> metadata;

}