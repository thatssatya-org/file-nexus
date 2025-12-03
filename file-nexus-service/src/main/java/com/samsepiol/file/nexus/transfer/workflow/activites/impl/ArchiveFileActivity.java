package com.samsepiol.file.nexus.transfer.workflow.activites.impl;

import com.samsepiol.file.nexus.content.config.StoreConfigRegistry;
import com.samsepiol.file.nexus.content.config.StoreTransferConfig;
import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import com.samsepiol.file.nexus.transfer.config.FileTransferRequestConfig;
import com.samsepiol.file.nexus.transfer.workflow.activites.IArchiveFileActivity;
import com.samsepiol.file.nexus.transfer.workflow.activites.request.ArchiveFileActivityRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.samsepiol.file.nexus.transfer.workflow.helper.FileTransferHelper.findFileTransferConfig;
import static com.samsepiol.file.nexus.transfer.workflow.helper.FileTransferHelper.getNewFilePath;


@Slf4j
@RequiredArgsConstructor
public class ArchiveFileActivity implements IArchiveFileActivity {

    private final StoreConfigRegistry storeConfigRegistry;

    @Override
    public void archive(ArchiveFileActivityRequest request) {
        StoreTransferConfig transferConfig = storeConfigRegistry.getConfig(request.getSourceStore(), request.getTargetStore());
        if(transferConfig.getFileTransferConfig().getSourceStorageType() == StorageType.SFTP) {
            FileTransferRequestConfig config = findFileTransferConfig(request.getRemoteFilePath(), transferConfig);
            //TODO: optimise this for different renaming functions for different stores
            transferConfig.getSourceClient().rename(request.getBucketName(), request.getRemoteFilePath(), getNewFilePath(request.getRemoteFilePath(), config.getArchiveDir()));
        } else {
            transferConfig.getSourceClient().deleteFile(request.getBucketName(), request.getRemoteFilePath());
        }
    }

}
