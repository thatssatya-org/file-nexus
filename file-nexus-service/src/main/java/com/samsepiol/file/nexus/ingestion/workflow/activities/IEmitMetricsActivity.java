package com.samsepiol.file.nexus.ingestion.workflow.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;

import java.time.Duration;

/**
 * Activity interface for emitting metrics.
 */
@ActivityInterface
public interface IEmitMetricsActivity {

    /**
     * Emit a metric when no files are found for a scheduled source.
     *
     * @param sourceName The name of the source
     * @param timestamp The timestamp when the polling occurred
     */
    @ActivityMethod
    void emitNoFilesFoundMetric(String sourceName, long timestamp);

    /**
     * Emit a metric when files are found for a scheduled source.
     *
     * @param sourceName The name of the source
     * @param fileCount Number of files found
     * @param timestamp The timestamp when the polling occurred
     */
    @ActivityMethod
    void emitFilesFoundMetric(String sourceName, int fileCount, long timestamp);


    ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(5))
            .setMaximumInterval(Duration.ofMinutes(1))
            .setMaximumAttempts(2)
                            .build())
            .build();

    void emmitNoWorkflowsStartedMetric(String sourceName, int size, long startTime);
}
