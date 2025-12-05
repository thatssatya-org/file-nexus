package com.samsepiol.file.nexus.transfer.workflow.activites.impl;

import com.samsepiol.file.nexus.transfer.config.StoreConfigRegistry;
import com.samsepiol.file.nexus.transfer.config.StoreTransferConfig;
import com.samsepiol.file.nexus.transfer.workflow.activites.IFetchFileNamesActivity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class FetchFileNamesActivity implements IFetchFileNamesActivity {

    private final StoreConfigRegistry storeConfigRegistry;

    @Override
    public List<String> getFileNames(String sourceStore, String targetStore) {
        StoreTransferConfig storeTransferConfig = storeConfigRegistry.getConfig(sourceStore,targetStore);
        String bucketName = storeTransferConfig.getFileTransferConfig().getSrcRootDir();
        return storeTransferConfig.getSourceClient().getAllFiles(bucketName);
    }
}