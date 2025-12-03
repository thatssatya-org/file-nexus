package com.samsepiol.file.nexus.transfer.workflow.activites;

import com.samsepiol.file.nexus.temporal.utils.Activities;
import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.Map;

@ActivityInterface
public interface ISendEventActivity extends TemporalActivity {

    @ActivityMethod
    void sendEvent(String eventName, Map<String, Object> eventMetadata);

    static ISendEventActivity create() {
        return Activities.newActivity(ISendEventActivity.class);
    }
}
