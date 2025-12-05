package com.samsepiol.file.nexus.transfer.storage.client;

import com.samsepiol.file.nexus.transfer.storage.CloseableInputStream;

import java.io.InputStream;
import java.util.List;

public interface IStorageClient {
    List<String> getAllFiles(String bucketName);
    void uploadFile(String bucketName, String remotePath, InputStream inputStream);
    CloseableInputStream getFileStream(String bucketName, String remotePath);
    void deleteFile(String bucketName, String remotePath);
    void rename(String bucketName, String remoteFilePath, String newFilePath);
    Boolean fileExists(String bucketName, String remoteFilePath);
}
