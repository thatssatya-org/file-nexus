package com.samsepiol.file.nexus.transfer.config;

import com.samsepiol.file.nexus.transfer.storage.client.IStorageClient;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreTransferConfig {

    private IStorageClient sourceClient;
    private IStorageClient targetClient;
    private FileTransferConfigEntry fileTransferConfig;
}
