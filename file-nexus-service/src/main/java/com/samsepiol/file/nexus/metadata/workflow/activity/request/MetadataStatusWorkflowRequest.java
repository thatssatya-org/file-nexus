package com.samsepiol.file.nexus.metadata.workflow.activity.request;


import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MetadataStatusWorkflowRequest {
    @NonNull
    ParsedFileMetaData parsedFileMetaData;

    @NonNull
    String topicName;

    @NonNull
    String groupId;
}
