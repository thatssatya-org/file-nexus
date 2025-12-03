package com.samsepiol.file.nexus.ingestion.workflow;

import com.samsepiol.file.nexus.ingestion.workflow.dto.FileIngestionWorkflowRequest;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Interface for the file ingestion workflow.
 * This workflow is triggered when new files are detected by storage hooks.
 */
@WorkflowInterface
public interface IFileIngestionWorkflow extends TemporalWorkflow {

    /**
     * Process a file from a source storage and send it to configured destinations.
     * 
     * @param request The workflow request containing source and destination information
     */
    @WorkflowMethod
    void processFile(FileIngestionWorkflowRequest request);
}
