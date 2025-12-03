package com.samsepiol.file.nexus.metadata.workflow.activity;

import com.samsepiol.file.nexus.metadata.workflow.activity.request.FetchEndOffsetsActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.response.FetchEndOffsetsActivityResponse;
import com.samsepiol.file.nexus.metadata.workflow.constants.UpdateMetaDataStatusWorkflowActivityPrefixes;
import com.samsepiol.file.nexus.temporal.utils.Activities;
import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.NonNull;

@ActivityInterface(namePrefix = UpdateMetaDataStatusWorkflowActivityPrefixes.FETCH_END_OFFSETS)
public interface FetchEndOffsetListActivity extends TemporalActivity {

    @ActivityMethod
    @NonNull
    FetchEndOffsetsActivityResponse execute(@NonNull FetchEndOffsetsActivityRequest request);

    static FetchEndOffsetListActivity create() {
        return Activities.newActivity(FetchEndOffsetListActivity.class);
    }
}
