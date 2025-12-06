package com.samsepiol.file.nexus.content.models.request;

import com.samsepiol.file.nexus.content.data.models.enums.FileContentType;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class FileContentSaveServiceRequest {

    @NonNull
    String fileId;

    @NonNull
    String fileType;

    @NonNull
    String fileName;

    @NonNull
    @Builder.Default
    List<Map.Entry<FileContentType, Object>> fileContents = Collections.emptyList();

}
