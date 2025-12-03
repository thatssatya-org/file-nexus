package com.samsepiol.file.nexus.transfer.workflow.activites;

import com.samsepiol.file.nexus.temporal.utils.Activities;
import com.samsepiol.file.nexus.transfer.workflow.activites.request.ArchiveFileActivityRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface IArchiveFileActivity {

    @ActivityMethod
    void archive(ArchiveFileActivityRequest request);

    static IArchiveFileActivity create() {
        return Activities.newActivity(IArchiveFileActivity.class);
    }
}
