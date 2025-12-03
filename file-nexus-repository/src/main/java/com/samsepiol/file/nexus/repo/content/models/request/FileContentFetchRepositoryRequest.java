package com.samsepiol.file.nexus.repo.content.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class FileContentFetchRepositoryRequest {
    @NonNull
    List<String> fileIds;
    @NonNull
    Map<String, String> query;
}
