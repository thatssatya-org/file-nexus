package com.samsepiol.file.nexus.metadata.workflow.activity.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
public class FetchEndOffsetsActivityRequest {
    @NonNull
    String topicName;

    @NonNull
    String groupId;

    @Builder
    @Jacksonized
    public FetchEndOffsetsActivityRequest(@NonNull String topicName, @NonNull String groupId) {
        this.topicName = topicName;
        this.groupId = groupId;
    }

}
