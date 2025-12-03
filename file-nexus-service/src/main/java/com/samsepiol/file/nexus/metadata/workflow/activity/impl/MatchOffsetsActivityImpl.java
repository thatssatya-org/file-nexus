package com.samsepiol.file.nexus.metadata.workflow.activity.impl;

import com.samsepiol.file.nexus.metadata.workflow.activity.MatchOffsetsActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.exception.KafkaPartitionCommittedOffsetMissingException;
import com.samsepiol.file.nexus.metadata.workflow.activity.exception.KafkaPartitionsEndOffsetsNotReachedException;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.MatchOffsetsActivityRequest;
import com.samsepiol.kafka.client.IConsumerClient;
import com.samsepiol.temporal.annotations.TemporalActivity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@TemporalActivity
@RequiredArgsConstructor
@Slf4j
public class MatchOffsetsActivityImpl implements MatchOffsetsActivity {
    private final IConsumerClient client;

    @Override
    public void execute(@NonNull MatchOffsetsActivityRequest request) {

        log.info("[MatchOffsetsActivity] Matching committed offsets with previous end offsets for topic: {}, groupId: {}",
                request.getTopicName(), request.getGroupId());

        Map<Integer, Long> committedOffsets = client.getCommittedOffsets(request.getTopicName(), request.getGroupId());

        Map<Integer, Long> filteredEndOffsets = getPartitionDataForNonZeroEndOffsets(request);

        validatePartitionPresenceInCommittedOffsets(filteredEndOffsets, committedOffsets);

        filteredEndOffsets.entrySet().stream()
                .filter(endOffsetEntry ->
                        isCommitedOffsetLessThanEndOffset(endOffsetEntry.getKey(), endOffsetEntry.getValue(), committedOffsets, request.getTopicName()))
                .findAny()
                .ifPresent(i -> {
                    throw KafkaPartitionsEndOffsetsNotReachedException.create();
                });

    }

    private static Map<Integer, Long> getPartitionDataForNonZeroEndOffsets(MatchOffsetsActivityRequest request) {
        return request.getEndOffsets().entrySet().stream()
                .filter(entry -> !NumberUtils.LONG_ZERO.equals(entry.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static void validatePartitionPresenceInCommittedOffsets(Map<Integer, Long> endOffsetsMap,
                                                                    Map<Integer, Long> commitedOffsets) {

        List<Integer> missingCommittedOffsetPartitionIds = endOffsetsMap.keySet().stream()
                .filter(partitionId -> !commitedOffsets.containsKey(partitionId))
                .toList();

        if (!missingCommittedOffsetPartitionIds.isEmpty()) {
            missingCommittedOffsetPartitionIds.forEach(partition ->
                    log.error("[MatchOffsetsActivity] Committed offset not found for partition: {}", partition));
            throw KafkaPartitionCommittedOffsetMissingException.create();
        }
    }

    private static boolean isCommitedOffsetLessThanEndOffset(Integer endOffsetPartition, Long endOffset, Map<Integer, Long> commitedOffsets, String topicName) {
        log.info("[Match offsets activity] End offset: {}, Commited offset {},, partition {} for topic {}", endOffset, commitedOffsets.get(endOffsetPartition), endOffsetPartition, topicName);
        return commitedOffsets.get(endOffsetPartition)
                .compareTo(endOffset) < 0;
    }

}
