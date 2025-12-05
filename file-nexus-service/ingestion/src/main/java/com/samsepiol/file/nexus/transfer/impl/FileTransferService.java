package com.samsepiol.file.nexus.transfer.impl;

import com.samsepiol.file.nexus.transfer.config.StoreConfigRegistry;
import com.samsepiol.file.nexus.transfer.config.StoreTransferConfig;
import com.samsepiol.file.nexus.transfer.exception.ConfigMissingException;
import com.samsepiol.file.nexus.models.enums.Error;
import com.samsepiol.file.nexus.models.transfer.response.StoreConnectivityResponse;
import com.samsepiol.file.nexus.transfer.IFileTransferService;
import com.samsepiol.file.nexus.transfer.models.request.FileTransferServiceRequest;
import com.samsepiol.file.nexus.transfer.storage.client.IStorageClient;
import com.samsepiol.file.nexus.transfer.workflow.IFileTransferWorkflow;
import com.samsepiol.file.nexus.transfer.workflow.dto.FileTransferWorkflowRequest;
import com.samsepiol.library.temporal.constants.Queues;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.samsepiol.file.nexus.transfer.util.FileTransferUtil.generateWorkflowId;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileTransferService implements IFileTransferService {

    private final WorkflowClient workflowClient;
    private final StoreConfigRegistry storeConfigRegistry;

    @Override
    public String transferFile(FileTransferServiceRequest request) {
        validateRequest(request);
        String workflowId = generateWorkflowId(request.getSourceStore(), request.getTargetStore());
        IFileTransferWorkflow fileTransferWorkflow = workflowClient.newWorkflowStub(
                IFileTransferWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(Queues.WORKFLOWS)
                        .build());

        FileTransferWorkflowRequest workflowRequest = createWorkflowRequest(request);
        log.info("[IFileTransferWorkflow] Start file transfer workflow with request {}", request);
        WorkflowClient.start(fileTransferWorkflow::transferFile, workflowRequest);
        return workflowId;
    }

    @Override
    public StoreConnectivityResponse checkStoreConnectivity(String storeName, String bucketName) {
        IStorageClient storageClient = storeConfigRegistry.getStorageClient(storeName);
        log.info("Checking Store Connectivity for  {}", storeName);
        if(Objects.nonNull(storageClient)) {
            try {
                List<String> filePaths = storageClient.getAllFiles(bucketName);
                log.info("FileNames are {}", filePaths.toString());
                return StoreConnectivityResponse.builder().message("Connection established Successfully").fileNames(filePaths).build();
            } catch (Exception e) {
                log.warn("Error in establishing SFTP connectivity: {}", e.getMessage());
                return StoreConnectivityResponse.builder().message("Error in establishing connection "+ e.getMessage()).build();
            }
        }
        return StoreConnectivityResponse.builder().message("Store config not found. Please check the name").build();

    }


    private void validateRequest(FileTransferServiceRequest request){
        log.info("Validating request");
        StoreTransferConfig storeTransferConfig = storeConfigRegistry.getConfig(request.getSourceStore(), request.getTargetStore());
        if(Objects.isNull(storeTransferConfig.getSourceClient())){
            throw ConfigMissingException.create(Error.SOURCE_STORE_CONFIG_MISSING);
        }
        if(Objects.isNull(storeTransferConfig.getTargetClient())){
            throw ConfigMissingException.create(Error.TARGET_STORE_CONFIG_MISSING);
        }
        if(Objects.isNull(storeTransferConfig.getFileTransferConfig())){
            throw ConfigMissingException.create(Error.FILE_STORE_CONFIG_MISSING);
        }
    }

    private FileTransferWorkflowRequest createWorkflowRequest(FileTransferServiceRequest request ){
        return FileTransferWorkflowRequest.builder()
                .sourceStore(request.getSourceStore())
                .targetStore(request.getTargetStore())
                .build();
    }
}
