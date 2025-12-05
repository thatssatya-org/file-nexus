package com.samsepiol.file.nexus.transfer.workflow.activites;

import com.samsepiol.file.nexus.temporal.utils.Activities;
import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

@ActivityInterface
public interface IFetchFileNamesActivity extends TemporalActivity {

    @ActivityMethod
    List<String> getFileNames(String sourceStore, String targetStore);

    static IFetchFileNamesActivity create() {
        return Activities.newActivity(IFetchFileNamesActivity.class);
    }
}
