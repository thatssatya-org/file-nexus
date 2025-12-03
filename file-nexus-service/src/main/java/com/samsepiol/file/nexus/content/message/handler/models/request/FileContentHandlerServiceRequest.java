package com.samsepiol.file.nexus.content.message.handler.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class FileContentHandlerServiceRequest {

    @NonNull
    String message;

    @NonNull
    Map<String,String> headers;
}
