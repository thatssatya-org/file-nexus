package com.samsepiol.file.nexus.metadata.workflow.activity.impl;

import com.samsepiol.file.nexus.metadata.workflow.activity.exception.KafkaPartitionCommittedOffsetMissingException;
import com.samsepiol.file.nexus.metadata.workflow.activity.exception.KafkaPartitionsEndOffsetsNotReachedException;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.MatchOffsetsActivityRequest;
import com.samsepiol.kafka.client.IConsumerClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MatchOffsetsActivityTest {
    private static final String TOPIC_NAME = "topic-name";
    private static final String GROUP_ID = "group-id";

    @Mock
    private IConsumerClient consumerClient;

    @InjectMocks
    private MatchOffsetsActivityImpl matchOffsetsActivity;

    @Test
    void testMissingCommittedOffsetsForGivenPartition() {
        var activityRequest = MatchOffsetsActivityRequest.builder()
                .topicName(TOPIC_NAME)
                .groupId(GROUP_ID)
                .endOffsets(Map.of(0, 1L, 1, 2L))
                .build();

        doReturn(Map.of(0, 1L))
                .when(consumerClient)
                .getCommittedOffsets(TOPIC_NAME, GROUP_ID);

        assertThrows(KafkaPartitionCommittedOffsetMissingException.class,
                () -> matchOffsetsActivity.execute(activityRequest));
    }

    @Test
    void testCommittedOffsetsMatchingToEndOffsetsForGivenPartition() {
        var activityRequest = MatchOffsetsActivityRequest.builder()
                .topicName(TOPIC_NAME)
                .groupId(GROUP_ID)
                .endOffsets(Map.of(0, 1L, 1, 2L))
                .build();

        doReturn(Map.of(0, 2L, 1, 2L))
                .when(consumerClient)
                .getCommittedOffsets(TOPIC_NAME, GROUP_ID);

        assertDoesNotThrow(() -> matchOffsetsActivity.execute(activityRequest));
    }

    @Test
    void testCommittedOffsetsLessThanEndOffsetsForGivenPartition() {
        var activityRequest = MatchOffsetsActivityRequest.builder()
                .topicName(TOPIC_NAME)
                .groupId(GROUP_ID)
                .endOffsets(Map.of(0, 1L, 1, 2L))
                .build();

        doReturn(Map.of(0, 2L, 1, 1L))
                .when(consumerClient)
                .getCommittedOffsets(TOPIC_NAME, GROUP_ID);

        assertThrows(KafkaPartitionsEndOffsetsNotReachedException.class, () -> matchOffsetsActivity.execute(activityRequest));
    }

    @Test
    void testMissingCommittedOffsetsWhenEndOffsetIsZeroForParticularPartition_01() {
        var activityRequest = MatchOffsetsActivityRequest.builder()
                .topicName(TOPIC_NAME)
                .groupId(GROUP_ID)
                .endOffsets(Map.of(0, 0L, 1, 2L))
                .build();

        doReturn(Map.of(1, 2L))
                .when(consumerClient)
                .getCommittedOffsets(TOPIC_NAME, GROUP_ID);

        assertDoesNotThrow(() -> matchOffsetsActivity.execute(activityRequest));
    }

    @Test
    void testMissingCommittedOffsetsWhenZeroEndOffsetForParticularPartition_02() {
        var activityRequest = MatchOffsetsActivityRequest.builder()
                .topicName(TOPIC_NAME)
                .groupId(GROUP_ID)
                .endOffsets(Map.of(0, 0L, 1, 2L))
                .build();

        doReturn(Map.of(1, 1L))
                .when(consumerClient)
                .getCommittedOffsets(TOPIC_NAME, GROUP_ID);

        assertThrows(KafkaPartitionsEndOffsetsNotReachedException.class, () -> matchOffsetsActivity.execute(activityRequest));
    }

    @Test
    void testMissingCommittedOffsetsWhenZeroEndOffsetForParticularPartition_03() {
        var activityRequest = MatchOffsetsActivityRequest.builder()
                .topicName(TOPIC_NAME)
                .groupId(GROUP_ID)
                .endOffsets(Map.of(0, 0L, 1, 2L, 2, 3L))
                .build();

        doReturn(Map.of(1, 1L))
                .when(consumerClient)
                .getCommittedOffsets(TOPIC_NAME, GROUP_ID);

        assertThrows(KafkaPartitionCommittedOffsetMissingException.class, () -> matchOffsetsActivity.execute(activityRequest));
    }
}