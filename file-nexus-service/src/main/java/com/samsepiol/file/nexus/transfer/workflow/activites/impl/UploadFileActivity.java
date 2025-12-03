package com.samsepiol.file.nexus.transfer.workflow.activites.impl;

import com.samsepiol.file.nexus.content.config.StoreConfigRegistry;
import com.samsepiol.file.nexus.content.config.StoreTransferConfig;
import com.samsepiol.file.nexus.content.exception.FileUploadException;
import com.samsepiol.file.nexus.transfer.config.FileTransferRequestConfig;
import com.samsepiol.file.nexus.transfer.storage.CloseableInputStream;
import com.samsepiol.file.nexus.transfer.storage.client.IStorageClient;
import com.samsepiol.file.nexus.transfer.workflow.activites.IUploadFileActivity;
import com.samsepiol.file.nexus.transfer.workflow.activites.request.UploadFileActivityRequest;
import com.samsepiol.file.nexus.transfer.workflow.dto.FileContext;
import com.samsepiol.temporal.annotations.TemporalActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BoundedInputStream;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.samsepiol.file.nexus.models.enums.Error.FILE_UPLOAD;
import static com.samsepiol.file.nexus.transfer.util.FileTransferUtil.getPath;
import static com.samsepiol.file.nexus.transfer.util.FileTransferUtil.isZipFile;
import static com.samsepiol.file.nexus.transfer.util.FileTransferUtil.validateFileName;
import static com.samsepiol.file.nexus.transfer.util.FileTransferUtil.validateFileSize;
import static com.samsepiol.file.nexus.transfer.workflow.helper.FileTransferHelper.findFileTransferConfig;

@Slf4j
@RequiredArgsConstructor
@TemporalActivity
public class UploadFileActivity implements IUploadFileActivity {

    private final StoreConfigRegistry storeConfigRegistry;

    @Override
    public void upload(UploadFileActivityRequest request) {
        StoreTransferConfig transferConfig = storeConfigRegistry.getConfig(request.getSourceStore(), request.getTargetStore());
        uploadFile(request, transferConfig);
    }

    private void uploadFile(UploadFileActivityRequest request, StoreTransferConfig transferConfig) {
        IStorageClient sourceClient = transferConfig.getSourceClient();
        IStorageClient targetClient = transferConfig.getTargetClient();

        try {
            if (!sourceClient.fileExists(null, request.getRemoteFilePath())) {
                log.warn("File does not exist: {}", request.getRemoteFilePath());
                return;
            }

            try (CloseableInputStream inputStreamObject =  sourceClient.getFileStream(null, request.getRemoteFilePath())) {
                InputStream inputStream = inputStreamObject.getDelegate();
                FileTransferRequestConfig fileConfig = findFileTransferConfig(request.getRemoteFilePath(), transferConfig);

                if (transferConfig.getFileTransferConfig().getExtractAndProcessZipEntries() && isZipFile(request.getRemoteFilePath())) {
                    processZipFile(inputStream, targetClient, fileConfig, request);
                } else {
                    processRegularFile(inputStream, targetClient, fileConfig, request);
                }
            }
        } catch (FileUploadException fe) {
            throw fe;
        } catch (Exception e) {
            log.error("Error uploading file ", e);
            throw FileUploadException.create(FILE_UPLOAD);
        }
    }

    private void processZipFile(InputStream inputStream, IStorageClient sourceClient,
                                FileTransferRequestConfig fileConfig,
                                UploadFileActivityRequest request) throws Exception {
        int fileSequence = 1;
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    log.info("Started uploading zip entry: {}", entry.getName());
                    validateFileName(entry.getName(), fileConfig.getRenameFileConfig().getOriginalPattern());
                    validateFileSize(entry.getSize());
                    String path = getPath(fileConfig, entry.getName() , FileContext.builder().index(fileSequence).build());
                    InputStream boundedStream = new BoundedInputStream(zis, entry.getSize()) {
                        @Override
                        public void close() {
                            // Do nothing to prevent closing ZipInputStream
                            log.info("Avoiding closure of inputstream");
                        }
                    };
                    sourceClient.uploadFile(fileConfig.getBucketName(), path, boundedStream);
                    log.info("Zip entry uploaded successfully to bucketName: {} and remotePath: {}",
                            fileConfig.getBucketPath(), path);
                    fileSequence++;
                }
            }
        }
    }

    private void processRegularFile(InputStream inputStream, IStorageClient sourceClient,
                                    FileTransferRequestConfig fileConfig,
                                    UploadFileActivityRequest request) throws Exception {
        log.info("Started uploading file: {}", request.getRemoteFilePath());
        String path = getPath(fileConfig, request.getRemoteFilePath(), null );
        sourceClient.uploadFile(fileConfig.getBucketName(), path, inputStream);
        log.info("File uploaded successfully to bucketName: {} and remotePath: {}",
                fileConfig.getBucketName(), path);
    }
}