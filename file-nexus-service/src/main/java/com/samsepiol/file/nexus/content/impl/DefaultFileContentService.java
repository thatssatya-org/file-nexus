package com.samsepiol.file.nexus.content.impl;


import com.samsepiol.file.nexus.content.FileContentService;
import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.data.FileContentDataService;
import com.samsepiol.file.nexus.content.data.models.response.FileContents;
import com.samsepiol.file.nexus.content.data.parser.FileContentParser;
import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import com.samsepiol.file.nexus.content.data.parser.models.request.FileContentParsingRequest;
import com.samsepiol.file.nexus.content.exception.DiscardedFileRequestedException;
import com.samsepiol.file.nexus.content.exception.FileContentParsingException;
import com.samsepiol.file.nexus.content.exception.FileContentRequestValidationException;
import com.samsepiol.file.nexus.content.exception.FileNotFoundException;
import com.samsepiol.file.nexus.content.exception.FileNotReadyForConsumptionException;
import com.samsepiol.file.nexus.content.exception.MandatoryColumnMissingException;
import com.samsepiol.file.nexus.content.exception.UnknownFileParserException;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.content.models.request.FileContentFetchServiceRequest;
import com.samsepiol.file.nexus.content.models.request.FileContentSaveServiceRequest;
import com.samsepiol.file.nexus.content.validation.FileContentRequestValidation;
import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.metadata.FileMetadataService;
import com.samsepiol.file.nexus.metadata.message.handler.models.response.FileMetadatas;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataFetchServiceRequest;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.samsepiol.file.nexus.content.mapper.FileContentServiceMapper.MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultFileContentService implements FileContentService {
    private final FileMetadataService fileMetadataService;
    private final FileContentDataService fileContentDataService;
    private final FileSchemaConfig fileSchemaConfig;
    private final Map<FileParserType, FileContentParser> fileContentParserMap;
    private final List<FileContentRequestValidation> requestValidations;

    @Override
    public @NonNull FileContents fetch(@NonNull FileContentFetchServiceRequest request) {
        runValidations(request);
        var metaDatas = getFileMetaDatas(request);
        validateMetaDataForServingContent(request, metaDatas);
        var fetchRequest = MAPPER.toFileContentFetchRequest(request, metaDatas.getFileIdsWithCompletedStatus());
        return fileContentDataService.fetch(fetchRequest);
    }

    private FileMetadatas getFileMetaDatas(FileContentFetchServiceRequest request) {
        var metaDataRequest = FileMetadataFetchServiceRequest.builder()
                .fileType(request.getFileType())
                .date(DateTimeUtils.toYYYYMMDD(request.getDate()))
                .build();

        return fileMetadataService.fetchMetadata(metaDataRequest);
    }

    private static void validateMetaDataForServingContent(FileContentFetchServiceRequest request, FileMetadatas metaDatas) {
        if (metaDatas.isEmpty()) {
            throw FileNotFoundException.create();
        }

        if (metaDatas.isIngestionPending()) {
            throw FileNotReadyForConsumptionException.create(request.getFileType(), request.getDate());
        }

        if (metaDatas.isDiscarded()) {
            throw DiscardedFileRequestedException.create(request.getFileType(), request.getDate());
        }
    }

    @Override
    public @NonNull FileContents save(@NonNull FileContentSaveServiceRequest request) throws UnsupportedFileException {

        var metaData = fileMetadataService.fetchMetadata(request.getFileId());

        if (!metaData.getFileName().equals(request.getFileName())) {
            // metricHelper.recordErrorMetric(Error.CONTENT_INGESTION_ATTEMPTED_FOR_DUPLICATE_FILE_ID.getCode(),
//                    Error.CONTENT_INGESTION_ATTEMPTED_FOR_DUPLICATE_FILE_ID.getMessage());
            log.warn("Content ingestion attempted for duplicate fileId: {} with fileName: {}", request.getFileId(), request.getFileName());
            return FileContents.empty();
        }

        if (metaData.isStatusDiscarded()) {
            log.warn("Content Ingestion attempted for discarded file: {}", request.getFileId());
            return FileContents.empty();
        }

        if (metaData.isStatusCompleted()) {
            log.warn("Content Ingestion attempted for completed file: {}", request.getFileId());
            return FileContents.empty();
        }

        var parsedFileContents = parseFileContents(request);
        var fileContentSaveRequest = MAPPER.toFileContentSaveRequest(request, parsedFileContents);
        return fileContentDataService.save(fileContentSaveRequest);
    }

    private List<Map<String, Object>> parseFileContents(FileContentSaveServiceRequest request)
            throws UnsupportedFileException {

        var mandatoryColumns = fileSchemaConfig.getMandatoryColumns(request.getFileType());

        return request.getFileContents().stream()
                .map(fileContent -> {
                    try {
                        return parseFileContent(request, mandatoryColumns, fileContent);
                    } catch (UnknownFileParserException | MandatoryColumnMissingException |
                             FileContentParsingException | UnsupportedFileException exception) {
                        log.error("[FileContentService]: {}", exception.getMessage());
                        throw FileNexusRuntimeException.wrap(exception);
                    }
                })
                .filter(Predicate.not(Map::isEmpty))
                .toList();
    }

    private Map<String, Object> parseFileContent(FileContentSaveServiceRequest request,
                                                 List<String> mandatoryColumns,
                                                 String fileContent)

            throws UnknownFileParserException, MandatoryColumnMissingException,
            FileContentParsingException, UnsupportedFileException {

        var parserType = fileSchemaConfig.getParserType(request.getFileType());

        var parsedContent = Optional.ofNullable(fileContentParserMap.get(parserType))
                .orElseThrow(UnknownFileParserException::create)
                .parse(FileContentParsingRequest.of(request.getFileType(), fileContent));
        if (parsedContent.isEmpty()) return Map.of();
        if (!mandatoryColumnsPresentInContent(mandatoryColumns, parsedContent)) {
            throw MandatoryColumnMissingException.create();
        }
        return parsedContent;
    }

    private static boolean mandatoryColumnsPresentInContent(List<String> mandatoryColumns,
                                                            Map<String, Object> map) {
        return allMandatoryKeysPresent(mandatoryColumns, map)
                && allValuesAreNonNullForMandatoryKeys(mandatoryColumns, map);
    }

    private static boolean allValuesAreNonNullForMandatoryKeys(List<String> mandatoryColumns, Map<String, Object> map) {
        return mandatoryColumns.stream().allMatch(column -> Objects.nonNull(map.get(column)));
    }

    private static boolean allMandatoryKeysPresent(List<String> mandatoryColumns, Map<String, Object> map) {
        return mandatoryColumns.stream().allMatch(map::containsKey);
    }

    private void runValidations(FileContentFetchServiceRequest request) {

        requestValidations.forEach(requestValidation -> {
            try {
                var response = requestValidation.validate(request);
                if (response.isInvalid()) {
                    throw FileContentRequestValidationException.create(response.getError());
                }

            } catch (UnsupportedFileException | FileContentRequestValidationException e) {
                throw FileNexusRuntimeException.wrap(e, Map.of("File type", request.getFileType()));
            }
        });
    }

    // TODO revisit
//    private String mapIndex(FileContentSaveRequest request, Index index) throws UnsupportedFileException {
//        var columnName = fileSchemaConfig.getColumnToIndex(request.getFileType(), index);
//        return Optional.ofNullable(request.getFileContents().get(0).get(columnName))
//                .map(String.class::cast)
//                .orElse(null);
//    }
//
//    private void sendEvent(FileContentSaveServiceRequest request, FileContentSaveRequest fileContentSaveRequest) throws UnsupportedFileException {
//        if (ObjectUtils.isEmpty(fileContentSaveRequest) || fileContentSaveRequest.getFileContents().isEmpty()) {
//            return;
//        }
//        String recordType = null;
//        try {
//            recordType = mapIndex(fileContentSaveRequest, Index.SECOND);
//        } catch (UnsupportedFileException ignored) {
//            // ignoring handling not required....
//        }
//        log.info("sending event for requestId : {} , fileContentSaveRequest : {}", request, fileContentSaveRequest);
//        String accountNumber = mapIndex(fileContentSaveRequest, Index.FIRST);
//        String[] filIdSplit = request.getFileId().split("-");
//        if (filIdSplit.length < 1 && StringUtils.isBlank(accountNumber)) {
//            return;
//        }
//        HashMap<String, String> hashMap = new HashMap<>();
//        LocalDate fileProcessingDate = DateTimeUtils.fromYYYYMMDD(Arrays.stream(filIdSplit).toList().get(1));
//        if (StringUtils.isNotBlank(recordType) && recordType.equals("CustomerDetails") && fileContentSaveRequest.getFileContents().get(0).containsKey("StatementDate")) {
//            hashMap.put("endDate", (String) fileContentSaveRequest.getFileContents().get(0).get("StatementDate"));
//        }
//        String fileProcessingDateStr = fileProcessingDate.toString();
//
//        String fileType = request.getFileType();
//        String fileName = request.getFileName();
//
//        if (Objects.nonNull(cache.get(CacheUtil.getStatementCacheKey(accountNumber, fileName)))) {
//            log.debug("Account {} already processed for file type {} and date {}, skipping event",
//                    accountNumber, fileType, fileProcessingDateStr);
//            return;
//        }
//        Map<String,String> filters;
//        if(StringUtils.isNotBlank(recordType)){
//            filters = Map.of(fileSchemaConfig.getColumnToIndex(request.getFileType(), Index.FIRST), accountNumber,fileSchemaConfig.getColumnToIndex(request.getFileType(), Index.SECOND),recordType);
//        }else{
//            filters = Map.of(fileSchemaConfig.getColumnToIndex(request.getFileType(), Index.FIRST), accountNumber);
//        }
//
//        FileContentFetchRequest fileContentFetchRequest = FileContentFetchRequest.builder()
//                .fileIds(List.of(request.getFileId()))
//                .fileType(fileType)
//                .filters(filters)
//                .build();
//
//        if (fileContentDataService.fetch(fileContentFetchRequest).getContents().isEmpty()) {
//            FileProcessingEventPayload payload = FileProcessingEventPayload.builder()
//                    .accountNumber(accountNumber)
//                    .fileProcessingDate(fileProcessingDateStr)
//                    .fileType(fileType)
//                    .recordType(recordType)
//                    .metadata(hashMap)
//                    .build();
//            // TODOeventHelper.publish("FILE_PROCESSING_LOG_" + fileType, payload);
//        }
//        cache.putWithTtl(CacheUtil.getStatementCacheKey(accountNumber, fileName), fileProcessingDateStr, Duration.ofSeconds(cacheConfig.getStatementFile().getTtl()));
//    }
}
