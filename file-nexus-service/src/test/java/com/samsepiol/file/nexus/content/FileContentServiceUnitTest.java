package com.samsepiol.file.nexus.content;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.data.FileContentDataService;
import com.samsepiol.file.nexus.content.data.models.FileContent;
import com.samsepiol.file.nexus.content.data.models.enums.Index;
import com.samsepiol.file.nexus.content.data.models.response.FileContents;
import com.samsepiol.file.nexus.content.data.parser.FileContentParser;
import com.samsepiol.file.nexus.content.data.parser.impl.JsonStringParser;
import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import com.samsepiol.file.nexus.content.data.parser.models.request.FileContentParsingRequest;
import com.samsepiol.file.nexus.content.exception.JsonFileContentParsingException;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.content.impl.DefaultFileContentService;
import com.samsepiol.file.nexus.content.models.request.FileContentFetchServiceRequest;
import com.samsepiol.file.nexus.content.models.request.FileContentSaveServiceRequest;
import com.samsepiol.file.nexus.content.validation.FileContentRequestValidation;
import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.metadata.FileMetadataService;
import com.samsepiol.file.nexus.metadata.message.handler.models.response.FileMetadata;
import com.samsepiol.file.nexus.metadata.message.handler.models.response.FileMetadatas;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataFetchServiceRequest;
import com.samsepiol.file.nexus.models.enums.Error;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import com.samsepiol.library.cache.Cache;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileContentServiceUnitTest {
    private static final String DATE = "20241001";
    private static final String FILE_ID_1 = "STATEMENT-20220222-1";
    private static final String FILE_TYPE = "sampleFileType";

    @Mock
    private FileSchemaConfig fileSchemaConfig;

    @Mock
    private FileMetadataService fileHandlerDataService;

    @Mock
    private FileContentDataService fileContentDataService;

    @Mock
    private List<FileContentRequestValidation> validations;

    @Mock
    private JsonStringParser jsonStringParser;

    @Mock
    private Map<FileParserType, FileContentParser> fileContentParserMap;

    @Mock
    private Cache<String, String> cache;

    @InjectMocks
    private DefaultFileContentService fileContentService;

    @Test
    void testFileContentsFetch() {
        doReturn(fileMetadatas())
                .when(fileHandlerDataService)
                .fetchMetadata(any(FileMetadataFetchServiceRequest.class));
//                .fetchMetadata(argThat(request -> request instanceof FileMetadataFetchServiceRequest serviceRequest &&  request.getFileType().equals(FILE_TYPE) && request.getDate().equals(DATE)));

        doReturn(fileContents())
                .when(fileContentDataService)
                .fetch(argThat(request -> request.getFileType().equals(FILE_TYPE) && request.getFileIds().contains(FILE_ID_1)));

        var serviceRequest = FileContentFetchServiceRequest.builder()
                .date(DateTimeUtils.fromYYYYMMDD(DATE))
                .fileType(FILE_TYPE)
                .filters(Map.of("key1", "value1", "key2", "value2"))
                .build();

        var fileContents = assertDoesNotThrow(() -> fileContentService.fetch(serviceRequest));

        assertEquals("{\"key1\":\"value1\"}", fileContents.getContents().get(0).getContent());
        assertEquals("{\"key2\":\"value2\"}", fileContents.getContents().get(1).getContent());

        verify(fileHandlerDataService, times(1))
                .fetchMetadata(any(FileMetadataFetchServiceRequest.class));
//                .fetchMetadata(argThat(request -> request.getFileType().equals(FILE_TYPE) && request.getDate().equals(DATE)));

        verify(fileContentDataService, times(1))
                .fetch(argThat(repoRequest -> repoRequest.getFileIds().contains(FILE_ID_1)));

    }

    @Test
    void testFileContentsSave() throws UnsupportedFileException, JsonFileContentParsingException {
        mockDependenciesForFileContentSave();
        doReturn(Map.of("key1", "value1"))
                .when(jsonStringParser)
                .parse(FileContentParsingRequest.of(FILE_TYPE, "{\"key1\":\"value1\"}"));

        doReturn(Map.of("key1", "value2"))
                .when(jsonStringParser)
                .parse(FileContentParsingRequest.of(FILE_TYPE, "{\"key1\":\"value2\"}"));
        var serviceRequest = FileContentSaveServiceRequest.builder()
                .fileType(FILE_TYPE)
                .fileId(FILE_ID_1)
                .fileName(FILE_ID_1)
                .fileContents(List.of("{\"key1\":\"value1\"}", "{\"key1\":\"value2\"}"))
                .build();
        doReturn("key1").when(fileSchemaConfig).getColumnToIndex("sampleFileType", Index.FIRST);
        doReturn("key2").when(fileSchemaConfig).getColumnToIndex("sampleFileType", Index.SECOND);
        doReturn(FileContents.builder().contents(List.of(FileContent.builder().build())).build()).when(fileContentDataService).fetch(any());

        assertDoesNotThrow(() -> fileContentService.save(serviceRequest));

        verify(fileContentDataService, times(1))
                .save(argThat(request -> request.getFileType().equals(FILE_TYPE) && request.getFileId().equals(FILE_ID_1)));
    }

    @Test
    void testFileContentsSaveWhenIngestionDiscarded() {
        doReturn(fileMetadataWithDiscardedStatus())
                .when(fileHandlerDataService)
                .fetchMetadata(FILE_ID_1);

        var serviceRequest = FileContentSaveServiceRequest.builder()
                .fileType(FILE_TYPE)
                .fileId(FILE_ID_1)
                .fileName(FILE_ID_1)
                .fileContents(List.of("{\"key1\":\"value1\"}", "{\"key1\":\"value2\"}"))
                .build();
        var fileContents = assertDoesNotThrow(() -> fileContentService.save(serviceRequest));

        assertTrue(fileContents.getContents().isEmpty());
        verify(fileContentDataService, times(0)).save(any());
    }

    @Test
    void testFileContentsSaveWhenIngestionCompleted() {
        doReturn(fileMetadata())
                .when(fileHandlerDataService)
                .fetchMetadata(FILE_ID_1);

        var serviceRequest = FileContentSaveServiceRequest.builder()
                .fileType(FILE_TYPE)
                .fileId(FILE_ID_1)
                .fileContents(List.of("{\"key1\":\"value1\"}", "{\"key1\":\"value2\"}"))
                .fileName(FILE_ID_1)
                .build();

        var fileContents = assertDoesNotThrow(() -> fileContentService.save(serviceRequest));

        assertTrue(fileContents.getContents().isEmpty());
        verify(fileContentDataService, times(0)).save(any());
    }

    @Test
    void testFileContentsSaveWhenMandatoryColumnMissing() throws JsonFileContentParsingException, UnsupportedFileException {
        mockDependenciesForFileContentSave();
        doReturn(Map.of("key3", "value3"))
                .when(jsonStringParser)
                .parse(FileContentParsingRequest.of(FILE_TYPE, "{\"key3\":\"value3\"}"));

        var serviceRequest = FileContentSaveServiceRequest.builder()
                .fileType(FILE_TYPE)
                .fileId(FILE_ID_1)
                .fileContents(List.of("{\"key3\":\"value3\"}", "{\"key2\":\"value2\"}", "{\"key4\":\"value4\"}"))
                .fileName(FILE_ID_1)
                .build();

        var exception = assertThrows(FileNexusRuntimeException.class, () -> fileContentService.save(serviceRequest));

        assertEquals(Error.MANDATORY_COLUMN_MISSING.getCode(), exception.getErrorCode());
        verify(fileContentDataService, times(0)).save(any());
    }

    private void mockDependenciesForFileContentSave() throws UnsupportedFileException {
        doReturn(fileMetadataWithPendingdStatus())
                .when(fileHandlerDataService)
                .fetchMetadata(FILE_ID_1);

        doReturn(List.of("key1"))
                .when(fileSchemaConfig)
                .getMandatoryColumns(FILE_TYPE);

        doReturn(FileParserType.JSON)
                .when(fileSchemaConfig)
                .getParserType(FILE_TYPE);

        doReturn(jsonStringParser)
                .when(fileContentParserMap)
                .get(FileParserType.JSON);
    }

    private static @NonNull FileContents fileContents() {
        return FileContents.builder()
                .contents(List.of(
                        FileContent.builder()
                                .id(FILE_ID_1 + "1")
                                .fileId(FILE_ID_1)
                                .content("{\"key1\":\"value1\"}")
                                .rowNumber("1")
                                .build(),
                        FileContent.builder()
                                .id(FILE_ID_1 + "2")
                                .fileId(FILE_ID_1)
                                .content("{\"key2\":\"value2\"}")
                                .rowNumber("2")
                                .build()))
                .build();
    }

    private static @NonNull FileMetadatas fileMetadatas() {
        return FileMetadatas.from(List.of(fileMetadata()));
    }

    private static FileMetadata fileMetadata() {
        return FileMetadata.builder()
                .id(FILE_ID_1)
                .fileName(FILE_ID_1)
                .status(MetadataStatus.COMPLETED)
                .fileType(FILE_TYPE)
                .date(DATE)
                .build();
    }

    private static FileMetadata fileMetadataWithDiscardedStatus() {
        return FileMetadata.builder()
                .id(FILE_ID_1)
                .fileName(FILE_ID_1)
                .status(MetadataStatus.DISCARDED)
                .fileType(FILE_TYPE)
                .date(DATE)
                .build();
    }

    private static FileMetadata fileMetadataWithPendingdStatus() {
        return FileMetadata.builder()
                .id(FILE_ID_1)
                .fileName(FILE_ID_1)
                .status(MetadataStatus.PENDING)
                .fileType(FILE_TYPE)
                .date(DATE)
                .build();
    }

}
