//package com.samsepiol.file.nexus.storage.hook.impl;
//
//import config.storage.com.samsepiol.file.nexus.HookConfigurationProvider;
//import hook.storage.com.samsepiol.file.nexus.AbstractStorageHook;
//import models.storage.com.samsepiol.file.nexus.FileInfo;
//import service.storage.com.samsepiol.file.nexus.FileStateService;
//import service.storage.com.samsepiol.file.nexus.TimestampTrackingService;
//import com.samsepiol.filestoragesystem.client.IFileStorageClient;
//import com.samsepiol.filestoragesystem.request.ListFilesRequest;
//import com.samsepiol.filestoragesystem.response.File;
//import com.samsepiol.filestoragesystem.response.ListFileResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
///**
// * Implementation of StorageHook for S3 storage.
// * Uses S3StorageClient to interact with S3.
// */
//@Slf4j
//@Component
//@Scope("prototype")
//public class S3Hook extends AbstractStorageHook {
//
//    private final IFileStorageClient s3Client;
//
//    private String bucketName;
//    private String hookName;
//
//    public S3Hook(TimestampTrackingService timestampTrackingService,
//                  IFileStorageClient s3Client,
//                  FileStateService fileStateService,
//                  HookConfigurationProvider hookConfigurationProvider) {
//        super(timestampTrackingService, fileStateService, hookConfigurationProvider);
//        this.s3Client = s3Client;
//    }
//
//    @Override
//    public void initializeWithBucketNameAndHookName(String bucketName, String hookName) {
//        log.info("Initializing S3Hook for bucket: {}, hookName: {}", bucketName, hookName);
//        this.bucketName = bucketName;
//        this.hookName = hookName;
//    }
//
//    @Override
//    protected List<FileInfo> listFiles(Instant lastProcessedTime, int maxResults, String prefix) {
//        log.debug("Listing files from S3 bucket: {} with maxResults: {}", bucketName, maxResults);
//        boolean isTruncated = true;
//        String nextPageToken = null;
//        final Set<FileInfo> fileInfoSet = new HashSet<>();
//        try {
//            while (isTruncated) {
//                log.debug("Requesting files from S3 bucket: {}, nextPageToken: {}", bucketName, nextPageToken);
//                ListFileResponse response = s3Client.listFiles(
//                        ListFilesRequest.builder()
//                                .bucket(bucketName)
//                                .maxResults(maxResults)
//                                .prefix(prefix)
//                                .nextPageToken(nextPageToken).build()
//                );
//
//                List<File> files = response.getFileList();
//
//                if (files != null && !files.isEmpty()) {
//                    for (File file : files) {
//                        Instant lastModified = file.getLastModified();
//                        if (lastProcessedTime == null || (lastModified != null && lastModified.isAfter(lastProcessedTime))) {
//                            fileInfoSet.add(FileInfo.builder()
//                                    .fileKey(file.getName())
//                                    .filePath(file.getPath())
//                                    .lastModified(lastModified)
//                                    .size(file.getSize())
//                                    .build());
//                        }
//                    }
//                }
//
//                log.debug("Processed page with nextPageToken: {}, isTruncated: {}", nextPageToken, isTruncated);
//                isTruncated = response.isTruncated();
//                nextPageToken = response.getNextPageToken();
//            }
//            return fileInfoSet.isEmpty() ? List.of() : new ArrayList<>(fileInfoSet);
//        } catch (Exception e) {
//            log.error("Error listing files from S3 bucket {}: {}", bucketName, e.getMessage(), e);
//            throw new RuntimeException( "Error listing files from S3 bucket: " + bucketName, e);
//        }
//    }
//
//    @Override
//    public InputStream getFileAsStream(String fileKey, long offsetByte) throws IOException {
//        log.debug("Retrieving file as stream from S3 bucket: {}, fileKey: {}", bucketName, fileKey);
//        try {
//            return s3Client.downloadFileAsStream(bucketName, fileKey, offsetByte);
//        } catch (IOException e) {
//            log.error("Error retrieving file as stream from S3 bucket {}: {}", bucketName, e.getMessage(), e);
//            throw new IOException("S3 file stream retrieval error", e);
//        }
//    }
//
//    @Override
//    public String getName() {
//        return hookName;
//    }
//}
