package com.samsepiol.file.nexus;

import com.samsepiol.file.nexus.transfer.workflow.helper.FileTransferHelper;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FileTransferHelperTest {

    @Test
    public void testGetNewFilePathWithExtension() {

        String remoteFilePath = "CC_Alliances_samsepiol/YCCP_STMT_ACCT_007.zip";
        String outputDir = "CC_Alliances_samsepiol/output/";

        String result = FileTransferHelper.getNewFilePath(remoteFilePath, outputDir);

        String timestampPattern = "\\d{4}-\\d{2}-\\d{2}_\\d{2}\\d{2}\\d{2}";
        assertTrue(result.matches(".*" + timestampPattern + "-processed\\.zip$"), "Result should include a properly formatted timestamp and processed extension");
    }

    @Test
    public void testGetNewFilePathWithoutExtension() {
        String remoteFilePath = "input/testFile";
        String outputDir = "output/";

        String result = FileTransferHelper.getNewFilePath(remoteFilePath, outputDir);

        assertTrue(result.startsWith(Paths.get(outputDir, "testFile").toString()), "Output directory and file name should be correct");
        assertTrue(result.endsWith("-processed"), "Result should end with -processed");
        assertFalse(result.contains("."), "Result should not include an extension");
    }

}
