package com.samsepiol.file.nexus.transfer.workflow.activites;

import com.samsepiol.file.nexus.temporal.utils.Activities;
import com.samsepiol.file.nexus.transfer.workflow.activites.request.UploadFileActivityRequest;
import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface IUploadFileActivity extends TemporalActivity {

    @ActivityMethod
    void upload(UploadFileActivityRequest request);

    static IUploadFileActivity create() {
        return Activities.newActivity(IUploadFileActivity.class);
    }
}
