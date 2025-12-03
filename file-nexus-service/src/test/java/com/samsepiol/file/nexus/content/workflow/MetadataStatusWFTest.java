package com.samsepiol.file.nexus.content.workflow;


import com.samsepiol.file.nexus.metadata.workflow.MetadataStatusWorkflow;
import com.samsepiol.file.nexus.metadata.workflow.activity.FetchEndOffsetListActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.MatchOffsetsActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.UpdateStatusActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.FetchEndOffsetsActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.MatchOffsetsActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.MetadataStatusWorkflowRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.UpdateMetadataStatusActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.response.FetchEndOffsetsActivityResponse;
import com.samsepiol.file.nexus.metadata.workflow.impl.MetadataStatusWorkflowImpl;
import com.samsepiol.file.nexus.temporal.utils.IdGeneratorUtil;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static com.samsepiol.file.nexus.content.MetadataTestUtil.createTestParsedFileMetadata;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

@ExtendWith(SpringExtension.class)
@Slf4j
class MetadataStatusWFTest {
    private FetchEndOffsetListActivity fetchEndOffsetsActivity;
    private MatchOffsetsActivity matchOffsetsActivity;
    private UpdateStatusActivity updateStatusActivity;

    private TestWorkflowEnvironment testEnv;
    private WorkflowClient workflowClient;
    private Worker workflowWorker;

    @BeforeEach
    public void setUp(){
        testEnv = TestWorkflowEnvironment.newInstance();
        workflowClient = testEnv.getWorkflowClient();
        workflowWorker = testEnv.newWorker("QUEUE");


        fetchEndOffsetsActivity = mock(FetchEndOffsetListActivity.class, withSettings().withoutAnnotations());
        matchOffsetsActivity = mock(MatchOffsetsActivity.class, withSettings().withoutAnnotations());
        updateStatusActivity = mock(UpdateStatusActivity.class, withSettings().withoutAnnotations());

        workflowWorker.registerWorkflowImplementationTypes(MetadataStatusWorkflowImpl.class);
        workflowWorker.registerActivitiesImplementations(fetchEndOffsetsActivity, matchOffsetsActivity, updateStatusActivity);
    }

    @AfterEach
    public void tearDown() {
        testEnv.close();
    }

    @Test
    @DisplayName("MSWFTest")
    void metadataStatusWFTest(){
        String fileId = "STATEMENT-20241007";
        String topicName = "fileHandler";
        String groupId = "consumergroupId";

        MetadataStatusWorkflowRequest request = MetadataStatusWorkflowRequest.builder()
                .topicName(topicName)
                .groupId(groupId)
                .parsedFileMetaData(createTestParsedFileMetadata())
                .build();

        Map<Integer, Long> endOffsets = new HashMap<>();
        endOffsets.put(1, 2L);

        FetchEndOffsetsActivityRequest fetchEndOffsetsActivityRequest = FetchEndOffsetsActivityRequest.builder()
                .topicName(topicName)
                .groupId(groupId)
                .build();

        MatchOffsetsActivityRequest matchOffsetsActivityRequest = MatchOffsetsActivityRequest.builder()
                .endOffsets(endOffsets)
                .topicName(topicName)
                .groupId(groupId)
                .build();

        UpdateMetadataStatusActivityRequest updateMetadataStatusActivityRequest = UpdateMetadataStatusActivityRequest.builder()
                .parsedFileMetaData(createTestParsedFileMetadata())
                .build();

        FetchEndOffsetsActivityResponse fetchEndOffsetsActivityResponse = FetchEndOffsetsActivityResponse.builder()
                .endOffsets(endOffsets)
                .build();

        doReturn(fetchEndOffsetsActivityResponse).when(fetchEndOffsetsActivity).execute(fetchEndOffsetsActivityRequest);
        doNothing().when(matchOffsetsActivity).execute(matchOffsetsActivityRequest);
        doNothing().when(updateStatusActivity).execute(updateMetadataStatusActivityRequest);

        testEnv.start();

        MetadataStatusWorkflow workflow = workflowClient.newWorkflowStub(
                MetadataStatusWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(IdGeneratorUtil.generateMetadataStatusWorkflowId(fileId))
                        .setTaskQueue(workflowWorker.getTaskQueue())
                        .build());

        workflow.updateMetadataStatus(request);

        verify(fetchEndOffsetsActivity, times(1)).execute(fetchEndOffsetsActivityRequest);
        verify(matchOffsetsActivity, times(1)).execute(matchOffsetsActivityRequest);
        verify(updateStatusActivity, times(1)).execute(updateMetadataStatusActivityRequest);

    }
}
