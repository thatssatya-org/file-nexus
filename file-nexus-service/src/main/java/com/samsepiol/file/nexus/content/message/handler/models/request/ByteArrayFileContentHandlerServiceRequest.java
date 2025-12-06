package com.samsepiol.file.nexus.content.message.handler.models.request;

import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
public class ByteArrayFileContentHandlerServiceRequest extends BaseFileContentHandlerServiceRequest {

    byte[] message;

}