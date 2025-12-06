package com.samsepiol.file.nexus.content.message.handler.models.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@SuperBuilder(builderMethodName = "parentBuilder")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BaseFileContentHandlerServiceRequest {

    @NonNull
    Map<String,String> metadata;
}
