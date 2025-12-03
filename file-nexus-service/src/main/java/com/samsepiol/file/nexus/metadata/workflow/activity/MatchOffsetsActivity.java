package com.samsepiol.file.nexus.metadata.workflow.activity;

import com.samsepiol.file.nexus.metadata.workflow.activity.request.MatchOffsetsActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.constants.UpdateMetaDataStatusWorkflowActivityPrefixes;
import com.samsepiol.file.nexus.temporal.utils.Activities;
import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.NonNull;

@ActivityInterface(namePrefix = UpdateMetaDataStatusWorkflowActivityPrefixes.MATCH_END_OFFSETS)
public interface MatchOffsetsActivity extends TemporalActivity {

    @ActivityMethod
    void execute(@NonNull MatchOffsetsActivityRequest request);

    static MatchOffsetsActivity create() {
        return Activities.newActivity(MatchOffsetsActivity.class);
    }
}
