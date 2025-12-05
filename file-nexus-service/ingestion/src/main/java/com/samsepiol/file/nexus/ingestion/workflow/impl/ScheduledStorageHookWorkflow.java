package com.samsepiol.file.nexus.ingestion.workflow.impl;

import com.samsepiol.file.nexus.ingestion.workflow.IScheduledStorageHookWorkflow;
import com.samsepiol.file.nexus.ingestion.workflow.activities.IEmitMetricsActivity;
import com.samsepiol.file.nexus.ingestion.workflow.activities.IScheduledSourcePollingActivity;
import com.samsepiol.file.nexus.ingestion.workflow.activities.ITriggerFileIngestionActivity;
import com.samsepiol.file.nexus.storage.models.PollResult;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ScheduledStorageHookWorkflow implements IScheduledStorageHookWorkflow {

    private final IScheduledSourcePollingActivity pollingActivity = Workflow.newActivityStub(
            IScheduledSourcePollingActivity.class, IScheduledSourcePollingActivity.ACTIVITY_OPTIONS
    );

    private final IEmitMetricsActivity metricsActivity = Workflow.newActivityStub(
            IEmitMetricsActivity.class, IEmitMetricsActivity.ACTIVITY_OPTIONS
    );

    private final ITriggerFileIngestionActivity triggerActivity = Workflow.newActivityStub(
            ITriggerFileIngestionActivity.class, ITriggerFileIngestionActivity.ACTIVITY_OPTIONS
    );

    @Override
    public void processScheduledSource(String sourceName) {
        long startTime = Workflow.currentTimeMillis();
        PollResult pollResult = pollingActivity.pollSource(sourceName);

        if (pollResult == null || pollResult.files().isEmpty()) {
            metricsActivity.emitNoFilesFoundMetric(sourceName, startTime);
            return;
        }
        metricsActivity.emitFilesFoundMetric(sourceName, pollResult.files().size(), startTime);

        List<String> workflowIds = triggerActivity.triggerFileIngestionWorkflows(sourceName, pollResult);

        if (workflowIds.isEmpty()) {
            metricsActivity.emmitNoWorkflowsStartedMetric(sourceName, pollResult.files().size(), startTime);
        }
    }
}
