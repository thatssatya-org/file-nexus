package com.samsepiol.file.nexus.ingestion.workflow.utils;

import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowOptions;

public class WorkflowOptionsRegistry {
    public static WorkflowOptions getIFileIngestionWorkflowOptions(String workflowId) {
        return WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                //TODO
//                .setTaskQueue(TemporalConstants.WORKFLOW_QUEUE)
                .setWorkflowIdReusePolicy(
                        WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
                .build();
    }

    public static WorkflowOptions getIKafkaFileProcessingWorkflowOptions(String workflowId) {
        return WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
//                .setTaskQueue(TemporalConstants.WORKFLOW_QUEUE)
                .setWorkflowIdReusePolicy(
                        WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
                .build();
    }

    public static WorkflowOptions getIScheduledStorageHookWorkflowOptions(String workflowId) {
        return WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
//                .setTaskQueue(TemporalConstants.WORKFLOW_QUEUE)
                .build();
    }
}
