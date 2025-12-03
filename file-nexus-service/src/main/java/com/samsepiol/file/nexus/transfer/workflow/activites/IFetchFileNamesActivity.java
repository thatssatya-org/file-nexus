package com.samsepiol.file.nexus.transfer.workflow.activites;

import com.samsepiol.file.nexus.temporal.utils.Activities;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

@ActivityInterface
public interface IFetchFileNamesActivity {

    @ActivityMethod
    List<String> getFileNames(String sourceStore, String targetStore);

    static IFetchFileNamesActivity create() {
        return Activities.newActivity(IFetchFileNamesActivity.class);
    }
}
