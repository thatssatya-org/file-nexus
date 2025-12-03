package com.samsepiol.file.nexus.transfer.workflow.helper;

import com.samsepiol.file.nexus.content.config.FileUploadConfig;
import com.samsepiol.file.nexus.content.config.StoreTransferConfig;
import com.samsepiol.file.nexus.transfer.config.FileTransferRequestConfig;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Slf4j
public class FileTransferHelper {

    public static FileTransferRequestConfig findFileTransferConfig(String remotePath, StoreTransferConfig storeTransferConfig ) {
        Path path = Paths.get(remotePath); // extract filename out of the path
        for (FileUploadConfig fileUploadConfig : storeTransferConfig.getFileTransferConfig().getFileUploadConfig()) {
            if (Pattern.matches(fileUploadConfig.getFileNameRegex(), path.getFileName().toString())) {
                return FileTransferRequestConfig.builder()
                        .bucketName(fileUploadConfig.getUploadBucketName())
                        .bucketPath(fileUploadConfig.getUploadBucketPath())
                        .archiveDir(fileUploadConfig.getArchiveDir())
                        .filePath(remotePath)
                        .renameFileConfig(fileUploadConfig.getRenameFileConfig())
                        .build();
            }
        }
        log.info("File regex did not match with any in config , filename : {}", path.getFileName());
        return FileTransferRequestConfig.builder()
                .bucketPath(storeTransferConfig.getFileTransferConfig().getDefaultUploadConfig().getTargetPath())
                .bucketName(storeTransferConfig.getFileTransferConfig().getDefaultUploadConfig().getBucketName())
                .archiveDir(storeTransferConfig.getFileTransferConfig().getDefaultUploadConfig().getArchiveDir())
                .filePath(remotePath).build();
    }
    public static String getNewFilePath(String remoteFilePath, String outputDir) {
        // append timestamp to avoid conflicts when renaming file
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        String formattedTimestamp = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));

        Path path = Paths.get(remoteFilePath);
        String fileName = path.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            String nameWithoutExtension = fileName.substring(0, lastDotIndex);
            String extension = fileName.substring(lastDotIndex);
            return Paths.get(outputDir, nameWithoutExtension +"_" + formattedTimestamp + "-processed" + extension).toString();
        } else {
            // File name without extension
            return Paths.get(outputDir, fileName+ "_" + formattedTimestamp + "-processed").toString();
        }
    }

}
