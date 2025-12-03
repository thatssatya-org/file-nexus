package com.samsepiol.file.nexus.ingestion.workflow.activities;

import com.samsepiol.file.nexus.storage.models.PollResult;
import com.samsepiol.temporal.annotations.TemporalWorkflow;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.activity.ActivityOptions;

/**
 * Activity interface for polling scheduled sources.
 */
@ActivityInterface
public interface IScheduledSourcePollingActivity {

    /**
     * Poll a scheduled source for new files.
     *
     * @param sourceName The name of the source to poll
     * @return List of file information found
     */
    @ActivityMethod
    PollResult pollSource(String sourceName);

    ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(java.time.Duration.ofMinutes(15))
            .setRetryOptions(io.temporal.common.RetryOptions.newBuilder()
                    .setInitialInterval(java.time.Duration.ofSeconds(5))
                    .setMaximumInterval(java.time.Duration.ofMinutes(1))
                    .setMaximumAttempts(3)
                    .build())
            .build();
}
