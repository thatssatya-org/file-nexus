package com.samsepiol.file.nexus.content.validation;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.content.models.request.FileContentFetchServiceRequest;
import com.samsepiol.file.nexus.content.validation.impl.FileTypeValidation;
import com.samsepiol.file.nexus.content.validation.impl.SupportedQueryColumnsValidation;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FileContentRequestValidationTest {
    private static final String DATE = "20241001";
    private static final String FILE_TYPE = "sampleFileType";

    @Mock
    private FileSchemaConfig fileSchemaConfig;

    @InjectMocks
    private FileTypeValidation fileTypeValidation;

    @InjectMocks
    private SupportedQueryColumnsValidation supportedQueryColumnsValidation;

    @Test
    void testValidFileTypeValidation() {
        doReturn(Set.of(FILE_TYPE))
                .when(fileSchemaConfig)
                .getSupportedFiles();

        var serviceRequest = FileContentFetchServiceRequest.builder()
                .date(DateTimeUtils.fromYYYYMMDD(DATE))
                .fileType(FILE_TYPE)
                .filters(Collections.emptyMap())
                .build();

        var validationResponse = assertDoesNotThrow(() -> fileTypeValidation.validate(serviceRequest));
        assertFalse(validationResponse.isInvalid());
    }

    @Test
    void testInvalidFileTypeValidation() {
        doReturn(Set.of())
                .when(fileSchemaConfig)
                .getSupportedFiles();

        var serviceRequest = FileContentFetchServiceRequest.builder()
                .date(DateTimeUtils.fromYYYYMMDD(DATE))
                .fileType(FILE_TYPE)
                .filters(Collections.emptyMap())
                .build();

        var validationResponse = assertDoesNotThrow(() -> fileTypeValidation.validate(serviceRequest));
        assertTrue(validationResponse.isInvalid());
    }

    @Test
    void testValidQueryColumnsValidation() throws UnsupportedFileException {
        doReturn(Set.of("key1"))
                .when(fileSchemaConfig)
                .getQuerySupportedColumns(FILE_TYPE);

        var serviceRequest = FileContentFetchServiceRequest.builder()
                .date(DateTimeUtils.fromYYYYMMDD(DATE))
                .fileType(FILE_TYPE)
                .filters(Map.of("key1", "value1"))
                .build();

        var validationResponse = assertDoesNotThrow(() -> supportedQueryColumnsValidation.validate(serviceRequest));
        assertFalse(validationResponse.isInvalid());
    }

    @Test
    void testInvalidQueryColumnsValidation() throws UnsupportedFileException {

        doReturn(Set.of())
                .when(fileSchemaConfig)
                .getQuerySupportedColumns(FILE_TYPE);

        var serviceRequest = FileContentFetchServiceRequest.builder()
                .date(DateTimeUtils.fromYYYYMMDD(DATE))
                .fileType(FILE_TYPE)
                .filters(Map.of("key1", "value1"))
                .build();

        var validationResponse = assertDoesNotThrow(() -> supportedQueryColumnsValidation.validate(serviceRequest));
        assertTrue(validationResponse.isInvalid());
    }

}