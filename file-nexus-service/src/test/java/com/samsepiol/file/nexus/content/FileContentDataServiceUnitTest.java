package com.samsepiol.file.nexus.content;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.data.impl.DefaultFileContentDataService;
import com.samsepiol.file.nexus.content.data.models.enums.Index;
import com.samsepiol.file.nexus.content.data.models.request.FileContentFetchRequest;
import com.samsepiol.file.nexus.content.data.models.request.FileContentSaveRequest;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import com.samsepiol.file.nexus.repo.content.FileContentRepository;
import com.samsepiol.file.nexus.repo.content.entity.FileContent;
import com.samsepiol.file.nexus.repo.content.models.request.FileContentSaveRepositoryRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileContentDataServiceUnitTest {
    private static final String FILE_ID_1 = "fileId1";
    private static final String FILE_TYPE = "sampleFileType";

    @Mock
    private FileSchemaConfig fileSchemaConfig;

    @Mock
    private FileContentRepository fileContentRepository;

    @InjectMocks
    private DefaultFileContentDataService fileContentDataService;

    @Test
    void testFetchContents() throws UnsupportedFileException {
        when(fileSchemaConfig.getIndexToColumn(FILE_TYPE, "key1")).thenReturn(Index.FIRST);
        doReturn(fileContents())
                .when(fileContentRepository)
                .fetch(argThat(repoRequest -> repoRequest.getFileIds().contains(FILE_ID_1)
                        && repoRequest.getQuery().containsKey("index1")));

        var fileContentFilters = Map.of("key1", "value1");

        var request = FileContentFetchRequest.builder()
                .fileIds(List.of(FILE_ID_1))
                .fileType(FILE_TYPE)
                .filters(fileContentFilters)
                .build();

        var fileContents = assertDoesNotThrow(() -> fileContentDataService.fetch(request));

        assertEquals("{\"key1\":\"value1\"}", fileContents.getContents().get(0).getContent());
        assertEquals("{\"key2\":\"value2\"}", fileContents.getContents().get(1).getContent());

        verify(fileContentRepository, times(1))
                .fetch(argThat(repoRequest -> repoRequest.getFileIds().contains(FILE_ID_1)));

    }

    @Test
    void testSaveContents() throws UnsupportedFileException {
        when(fileSchemaConfig.getColumnToIndex(FILE_TYPE, Index.FIRST)).thenReturn("key1");
        doReturn(fileContents())
                .when(fileContentRepository)
                .save(any(FileContentSaveRepositoryRequest.class));


        var request = FileContentSaveRequest.builder()
                .fileId(FILE_ID_1)
                .fileType(FILE_TYPE)
                .fileContents(List.of(Map.of("key1", "value1", "key2", "value2", "rowNumber", "1")))
                .build();

        var fileContents = assertDoesNotThrow(() -> fileContentDataService.save(request));

        assertEquals("{\"key1\":\"value1\"}", fileContents.getContents().get(0).getContent());
        assertEquals("{\"key2\":\"value2\"}", fileContents.getContents().get(1).getContent());

        verify(fileContentRepository, times(1))
                .save(any(FileContentSaveRepositoryRequest.class));
    }

    @Test
    void testSaveContentsWithMissingRowNumber() {
        var request = FileContentSaveRequest.builder()
                .fileId(FILE_ID_1)
                .fileType(FILE_TYPE)
                .fileContents(List.of(Map.of("key1", "value1", "key2", "value2")))
                .build();

        var exception = assertThrows(FileNexusRuntimeException.class, () -> fileContentDataService.save(request));
        assertEquals(Error.MISSING_ROW_NUMBER_FOR_FILE_CONTENT.getCode(), exception.getErrorCode());

    }

    private static @NonNull List<FileContent> fileContents() {
        return List.of(
                FileContent.builder()
                        .id(FILE_ID_1 + "1")
                        .fileId(FILE_ID_1)
                        .content("{\"key1\":\"value1\"}")
                        .rowNumber("1")
                        .index1("value1")
                        .createdAt(123456L)
                        .updatedAt(4356789L)
                        .build(),
                FileContent.builder()
                        .id(FILE_ID_1 + "2")
                        .fileId(FILE_ID_1)
                        .content("{\"key2\":\"value2\"}")
                        .rowNumber("2")
                        .index1("value1")
                        .createdAt(123456L)
                        .updatedAt(4356789L)
                        .build());
    }

}
