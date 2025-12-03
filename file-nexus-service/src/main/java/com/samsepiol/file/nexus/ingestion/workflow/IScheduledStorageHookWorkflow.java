package com.samsepiol.file.nexus.ingestion.workflow;

import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Interface for the scheduled file ingestion workflow.
 * This workflow is triggered by Temporal schedules for scheduled sources.
 */
@WorkflowInterface
public interface IScheduledStorageHookWorkflow extends TemporalWorkflow {

    /**
     * Process files from a scheduled source storage and send them to configured destinations.
     * 
     * @param sourceName The name of the source to process
     */
    @WorkflowMethod
    void processScheduledSource(String sourceName);
}
