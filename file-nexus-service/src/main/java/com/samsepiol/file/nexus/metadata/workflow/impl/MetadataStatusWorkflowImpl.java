package com.samsepiol.file.nexus.metadata.workflow.impl;

import com.samsepiol.file.nexus.metadata.workflow.MetadataStatusWorkflow;
import com.samsepiol.file.nexus.metadata.workflow.activity.FetchEndOffsetListActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.MatchOffsetsActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.UpdateStatusActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.FetchEndOffsetsActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.MatchOffsetsActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.MetadataStatusWorkflowRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.UpdateMetadataStatusActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.response.FetchEndOffsetsActivityResponse;
import com.samsepiol.temporal.annotations.TemporalWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@TemporalWorkflow
public class MetadataStatusWorkflowImpl implements MetadataStatusWorkflow {

    private final FetchEndOffsetListActivity fetchEndOffsetActivity = FetchEndOffsetListActivity.create();
    private final MatchOffsetsActivity matchOffsetsActivity = MatchOffsetsActivity.create();
    private final UpdateStatusActivity updateStatusActivity = UpdateStatusActivity.create();

    @Override
    public void updateMetadataStatus(MetadataStatusWorkflowRequest request) {
        var endOffsets = fetchEndOffsetActivity.execute(prepareFetchEndOffsetsActivityRequest(request.getTopicName(), request.getGroupId()));
        matchOffsetsActivity.execute(prepareMatchActivityRequest(request.getTopicName(), request.getGroupId(), endOffsets));
        updateStatusActivity.execute(prepareUpdateStatusActivityRequest(request));
    }

    private static UpdateMetadataStatusActivityRequest prepareUpdateStatusActivityRequest(MetadataStatusWorkflowRequest workflowRequest) {
        return UpdateMetadataStatusActivityRequest.builder()
                .parsedFileMetaData(workflowRequest.getParsedFileMetaData())
                .build();
    }

    private static FetchEndOffsetsActivityRequest prepareFetchEndOffsetsActivityRequest(String topicName, String groupId) {
        return FetchEndOffsetsActivityRequest.builder()
                .topicName(topicName)
                .groupId(groupId)
                .build();
    }

    private static MatchOffsetsActivityRequest prepareMatchActivityRequest(String topicName, String groupId,
                                                                           FetchEndOffsetsActivityResponse endOffsetsActivityResponse) {
        return MatchOffsetsActivityRequest.builder()
                .topicName(topicName)
                .groupId(groupId)
                .endOffsets(endOffsetsActivityResponse.getEndOffsets())
                .build();
    }

}
