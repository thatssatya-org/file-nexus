package com.samsepiol.file.nexus.content.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;
import java.util.Map;

@Value
@Builder
public class FileContentFetchServiceRequest {
    @NonNull
    String fileType;

    @NonNull
    LocalDate date;

    @NonNull
    Map<String, String> filters;
}
