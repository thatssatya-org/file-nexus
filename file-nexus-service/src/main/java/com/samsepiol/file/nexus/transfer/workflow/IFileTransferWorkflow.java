package com.samsepiol.file.nexus.transfer.workflow;

import com.samsepiol.file.nexus.transfer.workflow.dto.FileTransferWorkflowRequest;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface IFileTransferWorkflow extends TemporalWorkflow {
    @WorkflowMethod
    void transferFile(FileTransferWorkflowRequest request);
}
