package com.samsepiol.file.nexus.metadata.parser.type.impl;

import com.samsepiol.file.nexus.content.config.FileHandlerConfig;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.parser.exception.MandatoryFileNameSuffixMissingException;
import com.samsepiol.file.nexus.metadata.parser.type.models.FileSuffixParserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FileNameSuffixParserTest {
    public static final String FILE_NAME = "file_name_1";
    public static final String INCORRECT_FILE_NAME = "file_name";
    public static final String FILE_TYPE = "file";

    @Mock
    private FileHandlerConfig fileHandlerConfig;

    @InjectMocks
    private DefaultFileNameSuffixParser fileNameSuffixParser;

    @Test
    void testSuccessfulSuffixParsingWhenMandatory() throws UnsupportedFileException {
        mockFileNameSuffixes();
        mockSuffixMandatory(Boolean.TRUE);

        var parserRequest = prepareSuffixParserRequest(FILE_NAME);
        String suffix = assertDoesNotThrow(() -> fileNameSuffixParser.parse(parserRequest));
        assertEquals("1", suffix);
    }

    @Test
    void testSuccessfulSuffixParsingWhenNotMandatory() throws UnsupportedFileException {
        mockFileNameSuffixes();
        mockSuffixMandatory(Boolean.FALSE);

        var parserRequest = prepareSuffixParserRequest(FILE_NAME);
        String suffix = assertDoesNotThrow(() -> fileNameSuffixParser.parse(parserRequest));
        assertEquals("1", suffix);
    }

    @Test
    void testNullSuffixParsingWhenNotMandatory() throws UnsupportedFileException {
        mockFileNameSuffixes();
        mockSuffixMandatory(Boolean.FALSE);

        var parserRequest = prepareSuffixParserRequest(INCORRECT_FILE_NAME);
        String suffix = assertDoesNotThrow(() -> fileNameSuffixParser.parse(parserRequest));
        assertNull(suffix);
    }

    @Test
    void testExceptionToBeThrownWhenMandatorySuffixMissing() throws UnsupportedFileException {
        mockFileNameSuffixes();
        mockSuffixMandatory(Boolean.TRUE);

        var parserRequest = prepareSuffixParserRequest(INCORRECT_FILE_NAME);
        assertThrows(MandatoryFileNameSuffixMissingException.class, () -> fileNameSuffixParser.parse(parserRequest));
    }

    private static FileSuffixParserRequest prepareSuffixParserRequest(String fileName) {
        return FileSuffixParserRequest.builder()
                .fileName(fileName)
                .fileType(FILE_TYPE)
                .build();
    }

    private void mockSuffixMandatory(boolean mandatory) throws UnsupportedFileException {
        doReturn(mandatory)
                .when(fileHandlerConfig)
                .isSuffixMandatory(FILE_TYPE);
    }

    private void mockFileNameSuffixes() throws UnsupportedFileException {
        doReturn(List.of("1"))
                .when(fileHandlerConfig)
                .getSupportedFileNameSuffixes(FILE_TYPE);
    }
}