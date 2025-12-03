package com.samsepiol.file.nexus.repo.content.models.request;

import com.samsepiol.file.nexus.enums.FileStateStatus;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileStateSaveRepositoryRequest {
    
    @NonNull
    String filePath;
    
    @NonNull
    String hookName;
    
    @NonNull
    FileStateStatus status;
    
    @NonNull
    String stateKey;
}
