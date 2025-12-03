package com.samsepiol.file.nexus.content.data.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class FileContentSaveRequest {
    @NonNull
    String fileId;

    @NonNull
    String fileType;

    @NonNull
    @Builder.Default
    List<Map<String, Object>> fileContents = Collections.emptyList();

}
