package com.samsepiol.file.nexus.content.config;

import com.samsepiol.file.nexus.transfer.storage.client.GcpStorageClient;
import com.samsepiol.file.nexus.transfer.storage.client.IStorageClient;
import com.samsepiol.file.nexus.transfer.storage.client.S3StorageClient;
import com.samsepiol.file.nexus.transfer.storage.client.SftpStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreConfigRegistry {

    private static final String HYPHEN = "-";
    private final Map<String, IStorageClient> storeClient = new HashMap<>();
    private final Map<String, FileTransferConfigEntry> fileTransferConfigMap = new HashMap<>();
    private final StoreConfig storeConfig;
    private final FileTransferConfig fileTransferConfig;

    @PostConstruct
    public void initializeRegistry() {
        if(!Objects.isNull(fileTransferConfig)){
            for(FileTransferConfigEntry fileTransferConfigEntry: fileTransferConfig.getFileTransferConfigs()){
                fileTransferConfigMap.put(fileTransferConfigEntry.getIdentifier(), fileTransferConfigEntry);
            }
        }
        if (storeConfig.getStoreConfigEntry() != null) {
            for (StoreConfigEntry config : storeConfig.getStoreConfigEntry()) {
                storeClient.put(config.getIdentifier(), createStorageClient(config));
            }
        }
    }

    public IStorageClient createStorageClient(StoreConfigEntry storeConfigEntry){
        return switch (storeConfigEntry.getStorageType()){
            case S3 -> new S3StorageClient();
            case GCS -> new GcpStorageClient();
            case SFTP -> new SftpStorageClient(storeConfigEntry.getHost(),storeConfigEntry.getPrivateKey(), storeConfigEntry.getPassphrase(),
                    storeConfigEntry.getAliasName(), storeConfigEntry.getPort(), storeConfigEntry.getUsername(), storeConfigEntry.getRootDir());
            default -> throw new IllegalArgumentException("Unsupported storage type: " + storeConfigEntry.getStorageType());};
    }

    private String fetchFileTransferConfigId(String sourceIdentifier, String targetIdentifier){
        return sourceIdentifier + HYPHEN + targetIdentifier;
    }

    public IStorageClient getStorageClient(String identifier) {
        return storeClient.get(identifier);
    }

    public StoreTransferConfig getConfig(String sourceIdentifier, String targetIdentifier) {
        IStorageClient sourceClient = storeClient.get(sourceIdentifier);
        IStorageClient targetClient = storeClient.get(targetIdentifier);
        FileTransferConfigEntry configEntry = fileTransferConfigMap.get(fetchFileTransferConfigId(sourceIdentifier, targetIdentifier));
        return StoreTransferConfig.builder()
                .sourceClient(sourceClient)
                .targetClient(targetClient)
                .fileTransferConfig(configEntry)
                .build();
    }
}
