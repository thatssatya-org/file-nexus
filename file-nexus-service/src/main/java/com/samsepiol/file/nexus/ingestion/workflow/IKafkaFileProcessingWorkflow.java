package com.samsepiol.file.nexus.ingestion.workflow;

import com.samsepiol.file.nexus.models.request.KafkaFileProcessingWorkflowRequest;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface IKafkaFileProcessingWorkflow extends TemporalWorkflow {

    @WorkflowMethod
    void processFileToKafka(KafkaFileProcessingWorkflowRequest request);
}
