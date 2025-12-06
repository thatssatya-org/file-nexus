package com.samsepiol.file.nexus.content.message.handler.impl;

import com.samsepiol.file.nexus.content.FileContentService;
import com.samsepiol.file.nexus.content.data.models.enums.FileContentType;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.content.message.handler.FileContentConsumerService;
import com.samsepiol.file.nexus.content.message.handler.models.request.FileContentHandlerServiceRequest;
import com.samsepiol.file.nexus.content.models.request.FileContentSaveServiceRequest;
import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.metadata.FileMetadataService;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataSaveRequest;
import com.samsepiol.file.nexus.metadata.parser.FileMetaDataParsingService;
import com.samsepiol.file.nexus.metadata.parser.exception.FileMetaDataParsingException;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingFromHeadersRequest;
import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.samsepiol.file.nexus.enums.MetadataStatus.DISCARDED;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultFileContentConsumerService implements FileContentConsumerService {
    private static final List<Integer> VALIDATION_ERRORS = List.of(Error.MANDATORY_COLUMN_MISSING.getCode());

    private final FileMetadataService fileMetadataService;
    private final FileContentService fileContentService;
    private final FileMetaDataParsingService fileMetaDataParsingService;

    @Override
    public void handle(FileContentHandlerServiceRequest request) {
        var optionalParsedFileMetaData = parseAndSaveFileMetaData(request);

        optionalParsedFileMetaData.ifPresent(parsedFileMetaData -> {
            try {
                var contentSaveRequest = FileContentSaveServiceRequest.builder()
                        .fileId(parsedFileMetaData.getFileId())
                        .fileType(parsedFileMetaData.getFileType())
                        .fileName(parsedFileMetaData.getName())
                        .fileContents(prepareFileContents(request))
                        .build();

                fileContentService.save(contentSaveRequest);
            } catch (UnsupportedFileException exception) {
                log.info("Unknown file encountered, hence skipping saving content...");
                recordMetric(exception);
            } catch (FileNexusRuntimeException runtimeException) {
                handleFileNexusRuntimeException(parsedFileMetaData, runtimeException);
            }
        });

    }

    private static List<Map.Entry<FileContentType, Object>> prepareFileContents(FileContentHandlerServiceRequest request) {
        return List.of(Map.entry(FileContentType.PLAIN_STRING, request.getMessage()));
    }

    private void handleFileNexusRuntimeException(ParsedFileMetaData parsedFileMetaData,
                                                 FileNexusRuntimeException runtimeException) {

        log.error("[FileContentsConsumer]: {}", runtimeException.getMessage());
        // metricHelper.recordErrorMetric(runtimeException.getErrorCode(), runtimeException.getMessage());

        if (VALIDATION_ERRORS.contains(runtimeException.getErrorCode())) {
            log.info("Validation error occurred, updating metadata status to DISCARDED for fileId: {}", parsedFileMetaData.getFileId());
            fileMetadataService.updateStatus(parsedFileMetaData.getFileId(), DISCARDED);
            return;
        }

        throw runtimeException;
    }

    private Optional<ParsedFileMetaData> parseAndSaveFileMetaData(FileContentHandlerServiceRequest request) {
        try {
            var parsedFileMetaData = fileMetaDataParsingService.parse(prepareMetaDataParsingRequest(request));
            var saveRequest = FileMetadataSaveRequest.forPending(parsedFileMetaData);
            fileMetadataService.save(saveRequest);
            return Optional.of(parsedFileMetaData);
        } catch (FileMetaDataParsingException exception) {
            log.error("File metadata parsing error", exception);
            recordMetric(exception);
            return Optional.empty();
        } catch (UnsupportedFileException exception) {
            log.info("Unknown file encountered, hence skipping parsing metadata...");
            recordMetric(exception);
            return Optional.empty();
        }
    }

    private void recordMetric(FileNexusException exception) {
        // metricHelper.recordErrorMetric(exception.getErrorCode(), exception.getMessage());
    }

    private static FileMetaDataParsingFromHeadersRequest prepareMetaDataParsingRequest(FileContentHandlerServiceRequest request) {
        return FileMetaDataParsingFromHeadersRequest.builder()
                .headers(request.getMetadata())
                .build();
    }

}
