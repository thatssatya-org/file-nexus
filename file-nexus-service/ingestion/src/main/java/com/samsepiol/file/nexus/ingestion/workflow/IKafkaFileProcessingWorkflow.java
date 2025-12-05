package com.samsepiol.file.nexus.ingestion.workflow;

import com.samsepiol.file.nexus.models.request.KafkaFileProcessingWorkflowRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface IKafkaFileProcessingWorkflow {

    @WorkflowMethod
    void processFileToKafka(KafkaFileProcessingWorkflowRequest request);
}
