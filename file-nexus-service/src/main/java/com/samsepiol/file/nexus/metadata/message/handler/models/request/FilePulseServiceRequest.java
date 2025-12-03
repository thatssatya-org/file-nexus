package com.samsepiol.file.nexus.metadata.message.handler.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class FilePulseServiceRequest {

    @NonNull
    String message;

    @NonNull
    Map<String,String> headers;
}
