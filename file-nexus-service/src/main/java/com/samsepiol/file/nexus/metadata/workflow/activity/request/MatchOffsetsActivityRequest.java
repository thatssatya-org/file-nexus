package com.samsepiol.file.nexus.metadata.workflow.activity.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
public class MatchOffsetsActivityRequest {
    @NonNull
    String topicName;

    @NonNull
    String groupId;

    @NonNull
    Map<Integer, Long> endOffsets;

    @Builder
    @Jacksonized
    public MatchOffsetsActivityRequest(@NonNull String topicName, @NonNull Map<Integer, Long> endOffsets, @NonNull String groupId) {
        this.topicName = topicName;
        this.groupId = groupId;
        this.endOffsets = endOffsets;
    }

}
