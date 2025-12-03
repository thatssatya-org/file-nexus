package com.samsepiol.file.nexus.transfer.storage.client;

import com.samsepiol.file.nexus.transfer.storage.CloseableInputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

@Slf4j
public class GcpStorageClient implements IStorageClient {

    @Override
    public List<String> getAllFiles(String bucketName) {
        return List.of();
    }

    @Override
    public void uploadFile(String bucketName, String remotePath, InputStream inputStream) {

    }

    @Override
    public CloseableInputStream getFileStream(String bucketName, String remotePath) {
        return null;
    }

    @Override
    public void deleteFile(String bucketName, String remotePath) {

    }

    @Override
    public void rename(String bucketName, String remoteFilePath, String newFilePath) {

    }

    @Override
    public Boolean fileExists(String bucketName, String remoteFilePath) {
        return null;
    }

}
