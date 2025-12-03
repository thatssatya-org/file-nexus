package com.samsepiol.file.nexus.temporal.utils;

import io.temporal.workflow.Workflow;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Activities {

    public static <T> @NonNull T newActivity(@NonNull Class<T> activityClass) {
        return Workflow.newActivityStub(activityClass,
                WorkFlowOptionConstants.DEFAULT_MAX_ATTEMPT_ACTIVITY_OPTIONS);
    }

}
