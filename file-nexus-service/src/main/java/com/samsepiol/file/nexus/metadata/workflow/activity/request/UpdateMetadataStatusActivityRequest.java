package com.samsepiol.file.nexus.metadata.workflow.activity.request;

import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
public class UpdateMetadataStatusActivityRequest {
    @NonNull
    ParsedFileMetaData parsedFileMetaData;

    @Builder
    @Jacksonized
    public UpdateMetadataStatusActivityRequest(@NonNull ParsedFileMetaData parsedFileMetaData) {
        this.parsedFileMetaData = parsedFileMetaData;
    }

}
