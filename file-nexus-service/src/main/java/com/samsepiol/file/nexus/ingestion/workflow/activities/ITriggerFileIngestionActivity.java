package com.samsepiol.file.nexus.ingestion.workflow.activities;

import com.samsepiol.file.nexus.storage.models.PollResult;
import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;

import java.time.Duration;
import java.util.List;

/**
 * Activity interface for triggering file ingestion workflows.
 */
@ActivityInterface
public interface ITriggerFileIngestionActivity extends TemporalActivity {

    ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(15))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setInitialInterval(Duration.ofSeconds(10))
                    .setMaximumInterval(Duration.ofMinutes(3))
                    .setMaximumAttempts(3)
                    .build())
            .build();


    /**
     * Trigger file ingestion workflows for the given source name and file information.
     * @param sourceName {@link String} representing the source name
     * @param pollResult {@link PollResult}
     * @return List of workflow IDs that were triggered
     */
    List<String> triggerFileIngestionWorkflows(String sourceName, PollResult pollResult);
}
