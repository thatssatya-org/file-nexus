package com.samsepiol.file.nexus.ingestion.workflow.activities;

import com.samsepiol.file.nexus.models.request.KafkaFileProcessingWorkflowRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;

import java.time.Duration;

@ActivityInterface
public interface IKafkaFileProcessingActivity {

    @ActivityMethod
    void processAndSendToKafka(KafkaFileProcessingWorkflowRequest request);

    ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(15))
            .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(60)
                            .build())
            .setHeartbeatTimeout(Duration.ofSeconds(60))
            .build();
}
