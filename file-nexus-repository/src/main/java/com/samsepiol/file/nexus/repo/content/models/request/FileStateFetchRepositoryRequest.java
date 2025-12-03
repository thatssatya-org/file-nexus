package com.samsepiol.file.nexus.repo.content.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileStateFetchRepositoryRequest {
    
    @NonNull
    String stateKey;
}
