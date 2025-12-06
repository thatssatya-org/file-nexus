package com.samsepiol.file.nexus.content.data.impl;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.data.FileContentDataService;
import com.samsepiol.file.nexus.content.data.models.enums.Index;
import com.samsepiol.file.nexus.content.data.models.request.FileContentFetchRequest;
import com.samsepiol.file.nexus.content.data.models.request.FileContentSaveRequest;
import com.samsepiol.file.nexus.content.data.models.response.FileContents;
import com.samsepiol.file.nexus.content.exception.MissingRowNumberForFileContentException;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.repo.content.FileContentRepository;
import com.samsepiol.file.nexus.repo.content.entity.FileContent;
import com.samsepiol.file.nexus.repo.content.models.request.FileContentFetchRepositoryRequest;
import com.samsepiol.file.nexus.repo.content.models.request.FileContentSaveRepositoryRequest;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.samsepiol.file.nexus.content.data.mapper.FileContentDataServiceMapper.MAPPER;
import static com.samsepiol.file.nexus.repo.constants.RepositoryConstants.FileMetadataEntityConstants.FILE_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultFileContentDataService implements FileContentDataService {
    private final FileContentRepository fileContentRepository;
    private final FileSchemaConfig fileSchemaConfig;
    private static final String ROW_NUMBER_KEY_IN_FILE_CONTENT = "rowNumber";

    @Override
    public FileContents save(FileContentSaveRequest request) {
        if (!request.getFileContents().isEmpty()) {
            var saveRequest = prepareContentSaveRequest(request);
            var fileContents = fileContentRepository.save(saveRequest);
            return MAPPER.from(fileContents);
        }

        return FileContents.empty();
    }

    @Override
    public FileContents fetch(FileContentFetchRequest request) {
        var fetchRepositoryRequest = prepareContentFetchRequest(request);
        var fileContents = fileContentRepository.fetch(fetchRepositoryRequest);
        return MAPPER.from(fileContents);
    }


    private FileContentSaveRepositoryRequest prepareContentSaveRequest(FileContentSaveRequest request) {
        var entities = request.getFileContents().stream()
                .map(prepareEntity(request))
                .toList();

        return FileContentSaveRepositoryRequest.from(entities);
    }

    private Function<Map<String, Object>, FileContent> prepareEntity(
            FileContentSaveRequest request) {

        return fileContent -> {
            try {
                Long createdAt = DateTimeUtils.currentTimeInEpoch();
                String rowNumber = getRowNumberFromContent(request, fileContent);
                return FileContent.builder()
                        .id(generateId(request.getFileId(), rowNumber))
                        .fileId(request.getFileId())
                        .rowNumber(rowNumber)
                        .content(filteredContents(fileContent))
                        .index1(mapIndex(request, fileContent, Index.FIRST))
                        .index2(mapIndex(request, fileContent, Index.SECOND))
                        .index3(mapIndex(request, fileContent, Index.THIRD))
                        .createdAt(createdAt)
                        .updatedAt(createdAt)
                        .build();
            } catch (UnsupportedFileException | MissingRowNumberForFileContentException exception) {
                log.error("[File Content Data Service]: ", exception);
                throw FileNexusRuntimeException.wrap(exception, Map.of("FileType", request.getFileType()));
            }
        };
    }

    private static Map<String, Object> filteredContents(Map<String, Object> fileContent) {
        return fileContent.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String getRowNumberFromContent(FileContentSaveRequest request, Map<String, Object> fileContent) throws MissingRowNumberForFileContentException {
        return Optional.ofNullable(fileContent.get(ROW_NUMBER_KEY_IN_FILE_CONTENT))
                .map(Object::toString)
                .orElseGet(() -> {
                    try {
                        if (fileSchemaConfig.isRowNumberMandatory(request.getFileType())) {
                            throw MissingRowNumberForFileContentException.create();
                        }
                    } catch (MissingRowNumberForFileContentException | UnsupportedFileException e) {
                        throw FileNexusRuntimeException.wrap(e);
                    }
                    return null;
                });
    }

    private String mapIndex(FileContentSaveRequest request,
                            Map<String, Object> fileContent, Index index) throws UnsupportedFileException {
        var columnName = fileSchemaConfig.getColumnToIndex(request.getFileType(), index);
        if (StringUtils.isEmpty(columnName)) return null;
        return fileContent.getOrDefault(columnName, null) == null ? null : fileContent.get(columnName).toString();
    }

    private FileContentFetchRepositoryRequest prepareContentFetchRequest(FileContentFetchRequest request) {
        var query = prepareQueryWithIndexedFields(request);
        return MAPPER.from(request.getFileIds(), query);
    }

    private Map<String, String> prepareQueryWithIndexedFields(FileContentFetchRequest request) {
        log.info("Creating Query before insert request, {}", request);
        return request.getFilters().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> {
                            try {
                                return fileSchemaConfig.getIndexToColumn(request.getFileType(), entry.getKey()).getValue();
                            } catch (UnsupportedFileException exception) {
                                throw FileNexusRuntimeException.wrap(exception, Map.of(FILE_TYPE, request.getFileType()));
                            }
                        },
                        Map.Entry::getValue));
    }

    private String generateId(String fileId, String rowNumber) {
        if (rowNumber.isBlank()) {
            return String.format("FILE-%s", fileId);
        }
        return String.format("FILE-%s-ROW-%s", fileId, rowNumber);
    }


}
