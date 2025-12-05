package com.samsepiol.file.nexus.transfer.storage.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.samsepiol.file.nexus.transfer.exception.S3Exception;
import com.samsepiol.file.nexus.models.enums.Error;
import com.samsepiol.file.nexus.transfer.storage.CloseableInputStream;
import com.samsepiol.file.nexus.transfer.storage.config.S3Config;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

@Slf4j
public class S3StorageClient implements IStorageClient {

    private static final AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard().withRegion(S3Config.builder().build().getRegion()).build() ;

    @Override
    public List<String> getAllFiles(String bucketName) {
        try {
            return amazonS3Client.listObjects(bucketName)
                    .getObjectSummaries()
                    .stream()
                    .map(S3ObjectSummary::getKey)
                    .toList();
        } catch ( Exception e) {
            log.warn("Error getting file names from s3", e);
            throw S3Exception.create(Error.S3_FILE_LISTING_ERROR);
        }
    }

    @Override
    public void uploadFile(String bucketName, String remotePath, InputStream inputStream) {
        try {
            log.info("Uploading file to S3: bucketName={}, remotePath={}", bucketName, remotePath);
            amazonS3Client.putObject(bucketName, remotePath, inputStream, new ObjectMetadata());
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw S3Exception.create(Error.S3_FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public CloseableInputStream getFileStream(String bucketName, String remotePath) {
        try {
            InputStream inputStream = amazonS3Client.getObject(bucketName, remotePath).getObjectContent();
            return CloseableInputStream.builder().delegate(inputStream).build();
        } catch (Exception e) {
            log.error("Error retrieving file stream from S3 for bucket: {}, path: {}", bucketName, remotePath, e);
            throw S3Exception.create(Error.S3_FILE_RETRIEVAL_ERROR);
        }
    }


    @Override
    public void deleteFile(String bucketName, String remotePath) {
        try {
            amazonS3Client.deleteObject(bucketName, remotePath);
            log.info("File deleted from S3: bucketName={}, remotePath={}", bucketName, remotePath);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
            throw S3Exception.create(Error.S3_FILE_DELETE_ERROR);
        }
    }

    @Override
    public void rename(String bucketName, String remoteFilePath, String newFilePath) {
        try {
            amazonS3Client.copyObject(bucketName, remoteFilePath, bucketName, newFilePath);
            amazonS3Client.deleteObject(bucketName, remoteFilePath);
            log.info("File renamed from {} to {}", remoteFilePath, newFilePath);
        } catch (Exception e) {
            log.error("Error renaming file in S3: {} to {}", remoteFilePath, newFilePath, e);
            throw S3Exception.create(Error.S3_FILE_RENAME_ERROR);
        }
    }

    @Override
    public Boolean fileExists(String bucketName, String remoteFilePath) {
        try {
            return amazonS3Client.doesObjectExist(bucketName, remoteFilePath);
        } catch (Exception e) {
            log.error("Error checking if file exists in S3: {}", remoteFilePath, e);
            throw S3Exception.create(Error.S3_FILE_NOT_FOUND_ERROR);
        }
    }
}
