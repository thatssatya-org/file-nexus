package com.samsepiol.file.nexus.ingestion.workflow.impl;

import com.samsepiol.file.nexus.ingestion.workflow.IKafkaFileProcessingWorkflow;
import com.samsepiol.file.nexus.ingestion.workflow.activities.IKafkaFileProcessingActivity;
import com.samsepiol.file.nexus.models.request.KafkaFileProcessingWorkflowRequest;
import io.temporal.workflow.Workflow;

public class KafkaFileProcessingWorkflowImpl implements IKafkaFileProcessingWorkflow {

    private final IKafkaFileProcessingActivity activity = Workflow.newActivityStub(IKafkaFileProcessingActivity.class,
            IKafkaFileProcessingActivity.ACTIVITY_OPTIONS );

    @Override
    public void processFileToKafka(KafkaFileProcessingWorkflowRequest request) {
        activity.processAndSendToKafka(request);
    }
}
