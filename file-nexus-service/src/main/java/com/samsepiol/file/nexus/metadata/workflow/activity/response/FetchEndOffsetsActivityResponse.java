package com.samsepiol.file.nexus.metadata.workflow.activity.response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
public class FetchEndOffsetsActivityResponse {
    @NonNull
    Map<Integer, Long> endOffsets;

    @Builder
    @Jacksonized
    public FetchEndOffsetsActivityResponse(@NonNull Map<Integer, Long> endOffsets) {
        this.endOffsets = endOffsets;
    }

}
