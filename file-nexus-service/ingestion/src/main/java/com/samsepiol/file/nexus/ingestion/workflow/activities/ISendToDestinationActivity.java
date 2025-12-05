package com.samsepiol.file.nexus.ingestion.workflow.activities;

import com.samsepiol.file.nexus.ingestion.workflow.dto.FileIngestionWorkflowRequest;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;

import java.time.Duration;

/**
 * Activity interface for sending files to destinations.
 */
@ActivityInterface
public interface ISendToDestinationActivity extends TemporalActivity {

    /**
     * Send a file to a destination using the complete source configuration.
     * This is the preferred method that uses the complete source configuration.
     * 
     * @param source The complete source storage configuration
     * @param fileInfo The path of the file in the source storage
     * @param destination The destination configuration
     * @return true if the file was sent successfully, false otherwise
     */
    @ActivityMethod
    boolean sendToDestinationWithConfig(String source,
                                        FileInfo fileInfo, String destination);

    /**
     * Mark a file as processed after it has been sent to all destinations.
     * @param request {@link FileIngestionWorkflowRequest} containing the source name and file information
     */
    @ActivityMethod
    void markFileAsProcessed(FileIngestionWorkflowRequest request);

    /**
     * Default activity options for sending files to destinations.
     * This includes a start-to-close timeout of 10 minutes and retry options.
     * The retry options specify an initial interval of 1 minute, a maximum interval of 6 hour,
     * a backoff coefficient of 2.0, and maximum retry for 3 days
     */
    ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(10))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setInitialInterval(Duration.ofMinutes(1))
                    .setMaximumInterval(Duration.ofHours(6))
                    .setBackoffCoefficient(2.0)
                    .setMaximumAttempts(25)
                    .build())
            .build();

    @ActivityMethod
    void markFileAsScheduled(FileIngestionWorkflowRequest request);
}
