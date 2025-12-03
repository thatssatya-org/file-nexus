package com.samsepiol.file.nexus.temporal;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.client.WorkflowOptions;
import lombok.NonNull;

/**
 * Exposes temporal functionalities for common usage across application
 * @author satyajitroy
 */
public interface TemporalService {

    WorkflowExecutionStatus getWorkflowStatus(@NonNull String workflowId);

    <T> T newWorkflow(@NonNull String workFlowId, @NonNull Class<T> workflowClass);

    <T> T newWorkflow(@NonNull  WorkflowOptions workflowOptions, @NonNull Class<T> workflowClass);
}
