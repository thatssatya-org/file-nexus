package com.samsepiol.file.nexus.models.enums;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Error {
    INTERNAL_SERVER_ERROR(9027001, "Internal Server Error", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_CONTENT_DB_READ_FAILURE(9027002, "File Contents Read failure", "File Contents Read failure", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_CONTENT_DB_WRITE_FAILURE(9027003, "File Contents Write failure", "File Contents Write failure", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_FILE(9027004, "Unsupported File", "Unsupported File", HttpStatus.BAD_REQUEST),
    UNKNOWN_FILE_PARSER(9027005, "Unknown File Parser Error", "Unknown File Parser Error", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_FILE_CONTENT_PARSING_FAILURE(9027006, "Unable to parse json file content", "Unable to parse json file content", HttpStatus.INTERNAL_SERVER_ERROR),
    MISSING_ROW_NUMBER_FOR_FILE_CONTENT(9027007, "Missing row number for file content", "Missing row number in file content", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_QUERY_COLUMN_FOR_FILE(9027008, "Unsupported query column for file", "Unsupported query column for file", HttpStatus.BAD_REQUEST),
    MANDATORY_COLUMN_MISSING(9027009, "Mandatory column missing in file", "Mandatory column missing in file", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_METADATA_DB_READ_FAILURE(9027010, "File Metadata Database Read failure", "File Metadata Database Read failure", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_METADATA_DB_WRITE_FAILURE(9027011, "File Metadata Database Write failure", "File Metadata Database Write failure", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_METADATA_DB_UPDATE_FAILURE(9027012, "File Metadata Database Update failure", "File Metadata Database Update failure", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_METADATA_CACHE_READ_FAILURE(9027013, "File Metadata Cache Read failure", "File Metadata Cache Read failure", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_METADATA_CACHE_WRITE_FAILURE(9027014, "File Metadata Cache Write failure", "File Metadata Cache Write failure", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_METADATA_CACHE_UPDATE_FAILURE(9027015, "File Metadata Cache Update failure", "File Metadata Cache Update failure", HttpStatus.INTERNAL_SERVER_ERROR),
    MISSING_FILE_NAME_IN_FILE_PULSE_STATUS(9027016, "Missing file name in file pulse status", "Missing file name in file pulse status", HttpStatus.INTERNAL_SERVER_ERROR),
    MISSING_FILE_NAME_IN_MESSAGE_HEADERS(9027017, "Missing file name in message headers", "Missing file name in message headers", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DATE_FROM_NAME_PARSING_ERROR(9027018, "File Date Parsing from Name failed", "File Date Parsing from Name failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND_ERROR(9027019, "File not found", "File not found", HttpStatus.NOT_FOUND),
    FILE_NOT_READY_FOR_CONSUMPTION_ERROR(9027020, "File not ready for consumption", "File not ready for consumption", HttpStatus.TOO_EARLY),
    DISCARDED_FILE_REQUESTED_ERROR(9027021, "Discarded file requested", "Discarded file requested", HttpStatus.BAD_REQUEST),
    KAFKA_PARTITIONS_END_OFFSETS_AND_COMMITED_OFFSETS_MISMATCH(9027022, "Mismatch while comparing kafka end and commited offsets", "Mismatch while comparing kafka end and commited offsets", HttpStatus.INTERNAL_SERVER_ERROR),
    CONTENT_INGESTION_ATTEMPTED_FOR_DUPLICATE_FILE_ID(9027023, "Content ingestion attempted for duplicate file id", "Content ingestion attempted for duplicate file id", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_METADATA_STATUS_UPDATE_ATTEMPTED_FOR_DUPLICATE_FILE_ID(9027024, "File metadata status update attempted for duplicate fileId", "File metadata status update attempted for duplicate fileId", HttpStatus.INTERNAL_SERVER_ERROR),
    KAFKA_PARTITION_COMMITTED_OFFSET_MISSING(9027025, "Committed offsets missing for kafka partition", "Committed offsets missing for kafka partition", HttpStatus.INTERNAL_SERVER_ERROR),
    MANDATORY_FILE_NAME_SUFFIX_MISSING(9027026, "Mandatory file name suffix missing", "Mandatory file name suffix missing", HttpStatus.INTERNAL_SERVER_ERROR),
    TUDF_FILE_CONFIG_LOADING_FAILURE(9027027, "Unable to load tudf file parser config", "Unable to load tudf file parser config", HttpStatus.INTERNAL_SERVER_ERROR),
    TUDF_FILE_CONTENT_PARSING_FAILURE(9027028, "Unable to parse tudf file content", "Unable to parse tudf file content", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_FILE_PARSER_CONFIG(9027029, "Unknown file parser config", "Unknown file parser config", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD(9027030, "File could not be uploaded", "File could not be uploaded", HttpStatus.INTERNAL_SERVER_ERROR),
    SFTP_SESSION_CREATION_FAILURE(9027031, "Jsch(SFTP) session for  creation failed", "Jsch(SFTP) session for  creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SFTP_CHANNEL_CREATION_FAILURE(9027032, "Jsch(SFTP) channel creation failed", "Jsch(SFTP) channel creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SFTP_FILE_FETCH_FAILED(9027033, "Failure while fetching files from SFTP", "Failure while fetching files from SFTP", HttpStatus.INTERNAL_SERVER_ERROR),
    SFTP_RENAME_FILE_COMMAND_FAILED(9027034, "Sftp rename file failed", "Sftp rename file failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SFTP_FILE_NOT_FOUND_ERROR(9027035, "File not found on SFTP Server", "File not found on SFTP server", HttpStatus.INTERNAL_SERVER_ERROR),
    SFTP_STREAMING_ERROR(9027036, "Error streaming file from SFTP server", "Error streaming file from SFTP server", HttpStatus.INTERNAL_SERVER_ERROR),
    STORAGE_CLIENT_CONFIG_MISSING(9027037, "Config is missing for the storage client", "Config is missing for the storage client", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_FILE_LISTING_ERROR(9027038, "Error in fetching file names from S3", "Error in fetching file names from S3", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_FILE_UPLOAD_ERROR(9027039, "Error uploading file in S3", "Error uploading file in S3", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_FILE_RETRIEVAL_ERROR(9027040, "Error in fetching inputStream of file from s3", "Error in fetching inputStream of file from s3", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_FILE_DELETE_ERROR(9027041, "Error in deleting file from s3", "Error in deleting file from s3", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_FILE_RENAME_ERROR(9027042, "Error in renaming file in s3", "Error in renaming file in s3", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_FILE_NOT_FOUND_ERROR(9027043, "File not found on s3 server", "File not found on S3 server", HttpStatus.INTERNAL_SERVER_ERROR),
    SOURCE_STORE_CONFIG_MISSING(9027044, "Source Store config is missing", "Source Store config is missing", HttpStatus.INTERNAL_SERVER_ERROR),
    TARGET_STORE_CONFIG_MISSING(9027045, "Target Store config is missing", "Target Store config is missing", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_STORE_CONFIG_MISSING(9027046, "Target Store config is missing", "Target Store config is missing", HttpStatus.INTERNAL_SERVER_ERROR),
    IMPLEMENTATION_PENDING(9027047, "Unimplemented method", "Unimplemented method", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_SIZE_EXCEEDS_MAX_SIZE(9027048, "File size exceeds the maximum allowed size", "File size exceeds the maximum allowed size", HttpStatus.INTERNAL_SERVER_ERROR),
    UNEXPECTED_FILE_NAME(9027049, "FileName does not follow pattern", "FileName does not follow pattern", HttpStatus.INTERNAL_SERVER_ERROR),


    ;

    @NonNull
    private final Integer code;

    @NonNull
    private final String message;

    @NonNull
    private final String displayMessage;

    @NonNull
    private final HttpStatus httpStatus;
}
