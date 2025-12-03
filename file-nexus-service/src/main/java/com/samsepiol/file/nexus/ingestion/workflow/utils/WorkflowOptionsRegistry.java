package com.samsepiol.file.nexus.ingestion.workflow.utils;

import com.samsepiol.library.temporal.constants.Queues;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowOptions;

public class WorkflowOptionsRegistry {
    public static WorkflowOptions getIFileIngestionWorkflowOptions(String workflowId) {
        return WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(Queues.WORKFLOWS)
                .setWorkflowIdReusePolicy(
                        WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
                .build();
    }

    public static WorkflowOptions getIKafkaFileProcessingWorkflowOptions(String workflowId) {
        return WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(Queues.WORKFLOWS)
                .setWorkflowIdReusePolicy(
                        WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
                .build();
    }

    public static WorkflowOptions getIScheduledStorageHookWorkflowOptions(String workflowId) {
        return WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(Queues.WORKFLOWS)
                .build();
    }
}
