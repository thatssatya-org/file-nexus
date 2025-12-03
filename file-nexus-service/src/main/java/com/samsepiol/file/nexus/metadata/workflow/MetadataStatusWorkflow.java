package com.samsepiol.file.nexus.metadata.workflow;

import com.samsepiol.file.nexus.metadata.workflow.activity.request.MetadataStatusWorkflowRequest;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MetadataStatusWorkflow extends TemporalWorkflow {

    @WorkflowMethod
    void updateMetadataStatus(MetadataStatusWorkflowRequest request);

}
