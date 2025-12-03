package com.samsepiol.file.nexus.temporal.utils;


import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkFlowOptionConstants {

    public static final ActivityOptions DEFAULT_ONE_ATTEMPT_ACTIVITY_OPTIONS =
            ActivityOptions.newBuilder()
                    // TODO
//                    .setTaskQueue(TemporalConstants.ACTIVITY_QUEUE)
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(1)
                            .build())
                    .setStartToCloseTimeout(Duration.ofMinutes(1))
                    .build();

    public static final ActivityOptions DEFAULT_MAX_ATTEMPT_ACTIVITY_OPTIONS =
            ActivityOptions.newBuilder()
                    .setRetryOptions(RetryOptions.getDefaultInstance())
                    .setStartToCloseTimeout(Duration.ofMinutes(1))
                    .build();

    public static final ActivityOptions DEFAULT_TWENTY_ATTEMPT_ACTIVITY_OPTIONS =
            ActivityOptions.newBuilder()
//                    .setTaskQueue(TemporalConstants.ACTIVITY_QUEUE)
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(20)
                            .setInitialInterval(Duration.ofSeconds(10))
                            .setMaximumInterval(Duration.ofMinutes(5))
                            .setBackoffCoefficient(2)
                            .build())
                    .setStartToCloseTimeout(Duration.ofMinutes(1))
                    .build();

}
