package com.samsepiol.file.nexus;

import com.samsepiol.file.nexus.transfer.exception.FileUploadException;
import com.samsepiol.file.nexus.transfer.config.FileTransferRequestConfig;
import com.samsepiol.file.nexus.transfer.config.RenameFileConfig;
import com.samsepiol.file.nexus.transfer.util.FileTransferUtil;
import com.samsepiol.file.nexus.transfer.workflow.dto.FileContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.samsepiol.file.nexus.models.enums.Error.UNEXPECTED_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class FileTransferUtilTest {
    private static final String TEST_FILE_NAME = "YCCP_STMT_ACCT_007_20250125.TXT";
    private static final String REGULAR_FILE_NAME = "SETTLEMENT_007_20250110.TXT";

    private static final String TEST_BUCKET_NAME = "sample-bucket";
    private static final String TEST_BUCKET_PATH = "remotePath/";
    private static final String ORIGINAL_FILE_PATTERN = "YCCP_STMT_ACCT_\\d+_(\\d{8})\\.TXT";

    private RenameFileConfig renameFileConfig = RenameFileConfig.builder()
            .filePattern("STATEMENT_{DATE}_{INDEX}")
            .originalPattern(ORIGINAL_FILE_PATTERN)
            .build();

    private FileTransferRequestConfig fileTransferRequestConfig = FileTransferRequestConfig.builder()
            .filePath(".*.TXT")
            .renameFileConfig(renameFileConfig)
            .archiveDir("output/")
            .bucketName(TEST_BUCKET_NAME)
            .bucketPath(TEST_BUCKET_PATH)
            .build();

    private FileTransferRequestConfig fileTransferConfigForRegularFile = FileTransferRequestConfig.builder()
            .filePath(".*.csv")
            .archiveDir("output/")
            .bucketName(TEST_BUCKET_NAME)
            .bucketPath(TEST_BUCKET_PATH)
            .build();



    @Test
    public void testIsZipFile_withZipFile_returnsTrue() {
        assertTrue(FileTransferUtil.isZipFile("file.zip"));
    }

    @Test
    public void testIsZipFile_withoutZipFile_returnsFalse() {
        assertFalse(FileTransferUtil.isZipFile("file.txt"));
    }

    @Test
    public void testGenerateWorkflowId_createsValidWorkflowId() {
        String sourceStore = "source";
        String targetStore = "target";

        String workflowId = FileTransferUtil.generateWorkflowId(sourceStore, targetStore);

        assertTrue(workflowId.startsWith(sourceStore + "-" + targetStore + "-"));
        assertNotNull(workflowId.substring(sourceStore.length() + targetStore.length() + 2)); // Checking if UUID is present
    }

    @Test
    public void testValidateFileName_validFileName() {
        String patternStr = ".*(\\d{8}).*";
        assertDoesNotThrow(() -> FileTransferUtil.validateFileName(TEST_FILE_NAME, patternStr));
    }

    @Test
    public void testValidateFileName_invalidFileName_throwsException() {
        String invalidFileName = "YCCP_STMT_ACCT_007_202511XX.TXT";
        String patternStr = ".*(\\d{8}).*";

         assertThrows(FileUploadException.class,
                () -> FileTransferUtil.validateFileName(invalidFileName, patternStr));
    }

    @Test
    public void testIsValidDate_validDate_returnsTrue() {
        assertTrue(FileTransferUtil.isValidDate("20250125"));
    }

    @Test
    public void testIsValidDate_invalidDate_returnsFalse() {
        assertFalse(FileTransferUtil.isValidDate("20221325"));
    }

    @Test
    public void testValidateFileSize_validFileSize() {
        FileTransferUtil.validateFileSize(100 * 1024 * 1024); // 100MB, should pass
    }

    @Test
    public void testValidateFileSize_exceedMaxFileSize_throwsException() {
        assertThrows(FileUploadException.class,
                () -> FileTransferUtil.validateFileSize(1500 * 1024 * 1024)); // 600MB, should throw exception
    }

    @Test
    public void testExtractDateStringFromFile_validDate_returnsDateString() {
        String dateString = FileTransferUtil.extractDateStringFromFile(TEST_FILE_NAME, ORIGINAL_FILE_PATTERN);
        assertEquals("20250125", dateString);
    }

    @Test
    public void testExtractDateStringFromFile_invalidDate_throwsException() {
        String fileName = "YCCP_STMT_ACCT_007_invalid_date.TXT";

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> FileTransferUtil.extractDateStringFromFile(fileName, ORIGINAL_FILE_PATTERN));
        assertEquals(exception.getErrorCode(), UNEXPECTED_FILE_NAME.getCode());
    }

    @Test
    public void testGetRenamedFilePath_generatesCorrectPath() {
        String renamedFilePath = FileTransferUtil.getPathWithRenamedFileName(fileTransferRequestConfig, "/rootFolder/"+TEST_FILE_NAME, 5);
        assertEquals(TEST_BUCKET_PATH + "STATEMENT_20250125_5.TXT", renamedFilePath);
    }

    @Test
    public void testGetPath_noRenameFileConfig_returnsOriginalPath() {
        String path = FileTransferUtil.getPath(fileTransferConfigForRegularFile, REGULAR_FILE_NAME, FileContext.builder().index(1).build());
        assertEquals(TEST_BUCKET_PATH + REGULAR_FILE_NAME, path);
    }

    @Test
    public void testGetPath_withRenameFileConfig_returnsRenamedPath() {
        String path = FileTransferUtil.getPath(fileTransferRequestConfig, TEST_FILE_NAME, FileContext.builder().index(1).build());
        assertEquals(TEST_BUCKET_PATH + "STATEMENT_20250125_1.TXT", path);
    }
}
