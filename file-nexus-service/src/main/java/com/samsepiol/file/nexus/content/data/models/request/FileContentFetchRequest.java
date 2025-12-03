package com.samsepiol.file.nexus.content.data.models.request;


import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class FileContentFetchRequest {
    @NonNull
    String fileType;

    @NonNull
    List<String> fileIds;

    @NonNull
    Map<String , String> filters;
}
