package com.samsepiol.file.nexus.transfer.util;

import com.samsepiol.file.nexus.transfer.exception.FileUploadException;
import com.samsepiol.file.nexus.transfer.config.FileTransferRequestConfig;
import com.samsepiol.file.nexus.transfer.workflow.dto.FileContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.ObjectUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.samsepiol.file.nexus.models.enums.Error.FILE_SIZE_EXCEEDS_MAX_SIZE;
import static com.samsepiol.file.nexus.models.enums.Error.UNEXPECTED_FILE_NAME;

@Slf4j
public class FileTransferUtil {

    private static final long MAX_FILE_SIZE = 1024 * 1024 * 1024; // Example: Max file size (1 GB)

    private static final String DATE_PLACEHOLDER = "{DATE}";
    private static final String INDEX_PLACEHOLDER = "{INDEX}";
    private static final String DATE_FORMATTER = "yyyyMMdd";
    private static final String DOT = ".";

    public static boolean isZipFile(String fileName){
        return fileName.toLowerCase().contains(".zip" );
    }

    public static String generateWorkflowId(String sourceStore, String targetStore){
        return sourceStore + "-" + targetStore + "-" + UUID.randomUUID();
    }

    public static void validateFileName(String fileName, String patternStr){
        log.info("Validating fileName {} against pattern {}", fileName, patternStr);
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.matches()) {
            String dateStr = matcher.group(1);
            if (!isValidDate(dateStr)) {
                log.error("Invalid date format in filename: {}" , dateStr);
                throw FileUploadException.create(UNEXPECTED_FILE_NAME);
            }
        } else {
            log.error("Date match not found in filename: {} with pattern {}" , fileName, pattern);
            throw FileUploadException.create(UNEXPECTED_FILE_NAME);
        }
    }

    public static boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATTER);
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static void validateFileSize(long fileSize) {
        if (fileSize > MAX_FILE_SIZE) {
            log.error("File size exceeds max size {}", fileSize);
            throw FileUploadException.create(FILE_SIZE_EXCEEDS_MAX_SIZE);
        }
    }
    public static String getPath(FileTransferRequestConfig config, String fileName, FileContext fileContext){
        String generatedPath;

        if(ObjectUtils.isEmpty(config.getRenameFileConfig())){
            generatedPath = getPathWithOriginalFileName(config, fileName);
        } else {
            generatedPath = getPathWithRenamedFileName(config, fileName, fileContext.getIndex());
        }
        log.info("The path generated for filename {} is {}", fileName, generatedPath);
        return generatedPath;
    }

    public static String getPathWithOriginalFileName(FileTransferRequestConfig config, String fileName){
        Path path = Paths.get(fileName);
        return config.getBucketPath() + path.getFileName().toString();
    }

    public static String getPathWithRenamedFileName(FileTransferRequestConfig config, String remoteFilePath, Integer index ){
        String filename = FilenameUtils.getName(remoteFilePath);
        String dateString = extractDateStringFromFile(filename, config.getRenameFileConfig().getOriginalPattern());


        String pattern = config.getRenameFileConfig().getFilePattern();
        String updatedFileName = pattern
                .replace(DATE_PLACEHOLDER, dateString)
                .replace(INDEX_PLACEHOLDER, index.toString());

        return config.getBucketPath() + updatedFileName.concat(DOT).concat(FilenameUtils.getExtension(filename));
    }

    public static String extractDateStringFromFile(String fileName, String originalPattern) {
        Pattern datePattern = Pattern.compile(originalPattern);
        Matcher matcher = datePattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1); // Extracts the date string
        }
        throw FileUploadException.create(UNEXPECTED_FILE_NAME);

    }
}
