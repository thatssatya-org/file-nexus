package com.samsepiol.file.nexus.metadata.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class FetchMetaDataEntityRequest {

    @NonNull
    String fileType;

    @NonNull
    String date;
}
