package com.samsepiol.file.nexus.content.data.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class FileContent {
    @NonNull
    String id;
    @NonNull
    String fileId;
    @NonNull
    String rowNumber;
    Map<String, Object> content;
}
