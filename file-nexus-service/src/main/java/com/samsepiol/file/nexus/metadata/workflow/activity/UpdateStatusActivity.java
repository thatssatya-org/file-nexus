package com.samsepiol.file.nexus.metadata.workflow.activity;


import com.samsepiol.file.nexus.metadata.workflow.activity.request.UpdateMetadataStatusActivityRequest;
import com.samsepiol.file.nexus.metadata.workflow.constants.UpdateMetaDataStatusWorkflowActivityPrefixes;
import com.samsepiol.file.nexus.temporal.utils.Activities;
import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.NonNull;

@ActivityInterface(namePrefix = UpdateMetaDataStatusWorkflowActivityPrefixes.UPDATE_FILE_METADATA_STATUS)
public interface UpdateStatusActivity extends TemporalActivity {

    @ActivityMethod
    void execute(@NonNull UpdateMetadataStatusActivityRequest request);

    static UpdateStatusActivity create() {
        return Activities.newActivity(UpdateStatusActivity.class);
    }
}
