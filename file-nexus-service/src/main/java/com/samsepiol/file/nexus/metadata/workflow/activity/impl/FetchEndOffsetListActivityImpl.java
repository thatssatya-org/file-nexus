package com.samsepiol.file.nexus.metadata.workflow.activity.impl;

import com.samsepiol.file.nexus.metadata.workflow.activity.FetchEndOffsetListActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.FetchEndOffsetsActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.response.FetchEndOffsetsActivityResponse;
import com.samsepiol.library.kafka.admin.offsets.OffsetMonitoringService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FetchEndOffsetListActivityImpl implements FetchEndOffsetListActivity {
    private final OffsetMonitoringService client;

    @Override
    public @NonNull FetchEndOffsetsActivityResponse execute(@NonNull FetchEndOffsetsActivityRequest request) {
        log.info("[FetchEndOffsetListActivity] Fetching end offsets for topic: {}, groupId: {}",
                request.getTopicName(), request.getGroupId());

        var endOffsets = client.getOffsets(request.getTopicName(), request.getGroupId())
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getEndOffset()));

        return FetchEndOffsetsActivityResponse.builder()
                .endOffsets(endOffsets)
                .endOffsets(Collections.emptyMap())
                .build();
    }
}
