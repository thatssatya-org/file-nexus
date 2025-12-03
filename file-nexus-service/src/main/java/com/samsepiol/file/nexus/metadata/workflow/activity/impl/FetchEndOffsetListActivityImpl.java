package com.samsepiol.file.nexus.metadata.workflow.activity.impl;

import com.samsepiol.file.nexus.metadata.workflow.activity.FetchEndOffsetListActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.FetchEndOffsetsActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.response.FetchEndOffsetsActivityResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;


@RequiredArgsConstructor
@Slf4j
public class FetchEndOffsetListActivityImpl implements FetchEndOffsetListActivity {
    // TODO
//    private final IConsumerClient client;

    @Override
    public @NonNull FetchEndOffsetsActivityResponse execute(@NonNull FetchEndOffsetsActivityRequest request) {
        log.info("[FetchEndOffsetListActivity] Fetching end offsets for topic: {}, groupId: {}",
                request.getTopicName(), request.getGroupId());

        return FetchEndOffsetsActivityResponse.builder()
//                .endOffsets(client.getEndOffsets(request.getTopicName(), request.getGroupId()))
                .endOffsets(Collections.emptyMap())
                .build();
    }
}
