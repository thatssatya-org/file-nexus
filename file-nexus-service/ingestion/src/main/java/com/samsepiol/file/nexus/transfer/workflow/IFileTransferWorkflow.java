package com.samsepiol.file.nexus.transfer.workflow;

import com.samsepiol.file.nexus.transfer.workflow.dto.FileTransferWorkflowRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface IFileTransferWorkflow {
    @WorkflowMethod
    void transferFile(FileTransferWorkflowRequest request);
}
