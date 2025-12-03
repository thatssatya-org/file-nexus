package com.samsepiol.file.nexus.metadata.workflow;

import com.samsepiol.file.nexus.metadata.workflow.activity.request.MetadataStatusWorkflowRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MetadataStatusWorkflow {

    @WorkflowMethod
    void updateMetadataStatus(MetadataStatusWorkflowRequest request);

}
