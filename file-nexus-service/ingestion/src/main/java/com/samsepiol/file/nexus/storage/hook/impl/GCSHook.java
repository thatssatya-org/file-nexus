//package com.samsepiol.file.nexus.storage.hook.impl;
//
//import checked.exception.com.samsepiol.file.nexus.FileNexusException;
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
// * Implementation of StorageHook for GCS storage.
// * Uses GcpStorageClient to interact with GCS.
// */
//@Slf4j
//@Component
//@Scope("prototype")
//public class GCSHook extends AbstractStorageHook {
//
//
//    private final IFileStorageClient gcsClient;
//    private String bucketName;
//    private String hookName;
//
//    public GCSHook(TimestampTrackingService timestampTrackingService,
//                   IFileStorageClient gcsClient,
//                   FileStateService fileStateService,
//                   HookConfigurationProvider hookConfigurationProvider) {
//        super(timestampTrackingService, fileStateService, hookConfigurationProvider);
//        this.gcsClient = gcsClient;
//    }
//
//    @Override
//    public void initializeWithBucketNameAndHookName(String bucketName, String hookName) {
//        log.info("Initializing GCSHook for bucket: {}, hookName: {}", bucketName, hookName);
//        this.bucketName = bucketName;
//        this.hookName = hookName;
//    }
//
//    @Override
//    protected List<FileInfo> listFiles(Instant lastProcessedTime, int maxResults, String prefix) {
//        log.debug("Listing files from GCS bucket: {} with maxResults: {}", bucketName, maxResults);
//        boolean isTruncated = false;
//        String nextPageToken = null;
//        final Set<FileInfo> fileList = new HashSet<>();
//        try {
//            while (!isTruncated) {
//                log.debug("Requesting files from S3 bucket: {}, nextPageToken: {}", bucketName, nextPageToken);
//                ListFileResponse response = gcsClient.listFiles(
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
//                            FileInfo fileInfo = FileInfo.builder()
//                                    .fileKey(file.getName())
//                                    .filePath(file.getPath())
//                                    .lastModified(lastModified)
//                                    .size(file.getSize())
//                                    .build();
//                            fileList.add(fileInfo);
//                        }
//                    }
//                }
//
//                log.debug("Processed page with nextPageToken: {}, isTruncated: {}", nextPageToken, isTruncated);
//                isTruncated = response.isTruncated();
//                nextPageToken = response.getNextPageToken();
//            }
//            return fileList.isEmpty() ? List.of() : new ArrayList<>(fileList);
//        } catch (Exception e) {
//            log.error("Error listing files from GCS bucket {}: {}", bucketName, e.getMessage(), e);
//            throw new RuntimeException("Error listing files from GCS bucket: " + bucketName, e);
//        }
//    }
//
//    @Override
//    public InputStream getFileAsStream(String fileKey, long offsetByte) throws IOException {
//        log.debug("Getting file as stream from GCS bucket: {}, fileKey: {}", bucketName, fileKey);
//        try {
//            return gcsClient.downloadFileAsStream(bucketName, fileKey, offsetByte);
//        } catch (IOException e) {
//            log.error("Error getting file as stream from GCS bucket {}: {}", bucketName, e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    @Override
//    public String getName() {
//        return hookName;
//    }
//}
