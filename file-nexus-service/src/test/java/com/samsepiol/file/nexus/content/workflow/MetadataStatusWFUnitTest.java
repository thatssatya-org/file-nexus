//package com.samsepiol.file.nexus.content.workflow;
//
//import enums.com.samsepiol.file.nexus.MetadataStatus;
//import metadata.com.samsepiol.file.nexus.FileMetadataService;
//import response.models.handler.message.metadata.com.samsepiol.file.nexus.FileMetadata;
//import request.models.metadata.com.samsepiol.file.nexus.FileMetadataSaveRequest;
//import utils.temporal.com.samsepiol.file.nexus.IdGeneratorUtil;
//import workflow.metadata.com.samsepiol.file.nexus.MetadataStatusWorkflow;
//import activity.workflow.metadata.com.samsepiol.file.nexus.FetchEndOffsetListActivity;
//import activity.workflow.metadata.com.samsepiol.file.nexus.MatchOffsetsActivity;
//import activity.workflow.metadata.com.samsepiol.file.nexus.UpdateStatusActivity;
//import impl.activity.workflow.metadata.com.samsepiol.file.nexus.FetchEndOffsetListActivityImpl;
//import impl.activity.workflow.metadata.com.samsepiol.file.nexus.MatchOffsetsActivityImpl;
//import impl.activity.workflow.metadata.com.samsepiol.file.nexus.UpdateStatusActivityImpl;
//import request.activity.workflow.metadata.com.samsepiol.file.nexus.MetadataStatusWorkflowRequest;
//import impl.workflow.metadata.com.samsepiol.file.nexus.MetadataStatusWorkflowImpl;
//import com.samsepiol.kafka.client.IConsumerClient;
//import com.samsepiol.temporal.util.TemporalConstants;
//import io.temporal.client.WorkflowClient;
//import io.temporal.client.WorkflowOptions;
//import io.temporal.testing.TestWorkflowEnvironment;
//import io.temporal.worker.Worker;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static content.com.samsepiol.file.nexus.MetadataTestUtil.createTestFileMetadata;
//import static content.com.samsepiol.file.nexus.MetadataTestUtil.createTestParsedFileMetadata;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(SpringExtension.class)
//class MetadataStatusWFUnitTest {
//    @Mock
//    private IConsumerClient client;
//    @Mock
//    private FileMetadataService fileHandlerDataService;
//
//    private FetchEndOffsetListActivity fetchEndOffsetsActivity;
//    private MatchOffsetsActivity matchOffsetsActivity;
//    private UpdateStatusActivity updateStatusActivity;
//    private TestWorkflowEnvironment testEnv;
//    private WorkflowClient workflowClient;
//    private Worker workflowWorker;
//
//    @BeforeEach
//    public void setup(){
//        testEnv = TestWorkflowEnvironment.newInstance();
//        workflowWorker = testEnv.newWorker(TemporalConstants.WORKFLOW_QUEUE);
//        workflowClient = testEnv.getWorkflowClient();
//
//        fetchEndOffsetsActivity = new FetchEndOffsetListActivityImpl(client);
//        matchOffsetsActivity = new MatchOffsetsActivityImpl(client);
//        updateStatusActivity = new UpdateStatusActivityImpl(fileHandlerDataService);
//
//        workflowWorker.registerWorkflowImplementationTypes(MetadataStatusWorkflowImpl.class);
//        workflowWorker.registerActivitiesImplementations(fetchEndOffsetsActivity, matchOffsetsActivity, updateStatusActivity);
//    }
//
//    @AfterEach
//    public void tearDown(){
//        testEnv.close();
//    }
//
//    @Test
//    @DisplayName("MSWFTest")
//    void metadataStatusWFTest2(){
//        String fileId = "STATEMENT-20241007";
//        String topicName = "fileHandler";
//        String groupId = "consumerGroupId";
//
//        MetadataStatusWorkflowRequest request = MetadataStatusWorkflowRequest.builder()
//                .topicName(topicName)
//                .groupId(groupId)
//                .parsedFileMetaData(createTestParsedFileMetadata())
//                .build();
//
//        FileMetadata metadata = createTestFileMetadata();
//
//        Map<Integer, Long> endOffsets = new HashMap<>();
//        endOffsets.put(1, 2L);
//
//        Map<Integer, Long> committedOffsets = new HashMap<>();
//        committedOffsets.put(1, 6L);
//
//        doReturn(endOffsets).when(client).getEndOffsets(topicName, groupId);
//        doReturn(committedOffsets).when(client).getCommittedOffsets(topicName, groupId);
//        doReturn(metadata).when(fileHandlerDataService).fetchMetadata(fileId);
//        doNothing().when(fileHandlerDataService).updateStatus(fileId, MetadataStatus.COMPLETED);
//
//        testEnv.start();
//
//        MetadataStatusWorkflow workflow = workflowClient.newWorkflowStub(
//                MetadataStatusWorkflow.class,
//                WorkflowOptions.newBuilder()
//                        .setWorkflowId(IdGeneratorUtil.generateMetadataStatusWorkflowId(fileId))
//                        .setTaskQueue(workflowWorker.getTaskQueue())
//                        .build());
//
//        workflow.updateMetadataStatus(request);
//
//        verify(client, times(1)).getEndOffsets(topicName, groupId);
//        verify(client, times(1)).getCommittedOffsets(topicName, groupId);
//
//        verify(fileHandlerDataService, times(1)).saveOrUpdate(FileMetadataSaveRequest.forCompleted(request.getParsedFileMetaData()));
//
//    }
//
//
//}
