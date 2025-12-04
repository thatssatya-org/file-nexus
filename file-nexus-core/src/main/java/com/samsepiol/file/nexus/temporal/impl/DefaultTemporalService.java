package com.samsepiol.file.nexus.temporal.impl;

import com.samsepiol.file.nexus.temporal.TemporalService;
import com.samsepiol.library.temporal.config.TemporalConnectionConfig;
import com.samsepiol.library.temporal.constants.Queues;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TODO Move to library
@Service
@RequiredArgsConstructor
public class DefaultTemporalService implements TemporalService {
    private final WorkflowClient workflowClient;
    private final TemporalConnectionConfig temporalConfig;
    private final WorkflowServiceStubs workflowServiceStubs;

    @Override
    public WorkflowExecutionStatus getWorkflowStatus(@NonNull String workflowId) {
        WorkflowServiceGrpc.WorkflowServiceBlockingStub stub = workflowServiceStubs.blockingStub();
        DescribeWorkflowExecutionRequest request =
                DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(temporalConfig.getNamespace())
                        .setExecution(WorkflowExecution.newBuilder().setWorkflowId(workflowId))
                        .build();
        DescribeWorkflowExecutionResponse response = stub.describeWorkflowExecution(request);
        return response.getWorkflowExecutionInfo().getStatus();
    }

    @Override
    public <T> T newWorkflow(@NonNull String workFlowId, @NonNull Class<T> workflowClass) {
        var options = WorkflowOptions.newBuilder()
                .setWorkflowId(workFlowId)
                .setTaskQueue(Queues.WORKFLOWS)
                .build();

        return newWorkflow(options, workflowClass);
    }

    @Override
    public <T> T newWorkflow(@NonNull WorkflowOptions workflowOptions, @NonNull Class<T> workflowClass) {
        return workflowClient.newWorkflowStub(workflowClass, workflowOptions);
    }

}
