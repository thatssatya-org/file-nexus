package com.samsepiol.file.nexus.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.samsepiol.file.nexus.models.enums.Error;
import com.samsepiol.file.nexus.transfer.exception.SftpServiceException;
import com.samsepiol.file.nexus.transfer.storage.client.SftpStorageClient;
import com.samsepiol.file.nexus.transfer.storage.config.SftpConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SftpClientTest {

    @Mock
    private SftpSessionManager sessionManager;

    @Mock
    private SftpConfig config;

    @Mock
    private Session session;

    @Mock
    private ChannelSftp channelSftp;

    @InjectMocks
    private SftpStorageClient sftpClient;

    private static final String bucketName = "bucket";


    @Test
    void testGetAllFiles() throws Exception {
        when(sessionManager.get()).thenReturn(session);
        when(session.openChannel("sftp")).thenReturn(channelSftp);
        when(config.getRootDir()).thenReturn("/root");
        when(channelSftp.ls("/root")).thenReturn(createMockedFileList());

        // Capture Argument
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);

        // Call the method
        var files = sftpClient.getAllFiles(bucketName);

        // Verification
        verify(channelSftp).ls(pathCaptor.capture());
        assertEquals("/root", pathCaptor.getValue());

        // Validate Results
        assertEquals(2, files.size());
        assertEquals("/root/file1.txt", files.get(0));
        assertEquals("/root/file2.txt", files.get(1));
    }

    @Test
    void testRename() throws Exception {
        // Mocking
        when(sessionManager.get()).thenReturn(session);
        when(session.openChannel("sftp")).thenReturn(channelSftp);

        // Call the method
        sftpClient.rename(bucketName,"/old/file/path.txt", "/new/file/path.txt");

        // Capture Arguments
        ArgumentCaptor<String> oldPathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPathCaptor = ArgumentCaptor.forClass(String.class);

        // Verification
        verify(channelSftp).rename(oldPathCaptor.capture(), newPathCaptor.capture());
        assertEquals("/old/file/path.txt", oldPathCaptor.getValue());
        assertEquals("/new/file/path.txt", newPathCaptor.getValue());
    }

    @Test
    void testFileExists() throws Exception {
        // Mocking
        when(sessionManager.get()).thenReturn(session);
        when(session.openChannel("sftp")).thenReturn(channelSftp);
        doNothing().when(channelSftp).lstat("/path/to/file.txt");

        // Call the method
        boolean exists = sftpClient.fileExists(bucketName,"/path/to/file.txt");

        // Capture Argument
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);

        // Verification
        verify(channelSftp).lstat(pathCaptor.capture());
        assertEquals("/path/to/file.txt", pathCaptor.getValue());

        // Validate Results
        assertTrue(exists);
    }

    @Test
    void testFileExistsWhenFileDoesNotExist() throws Exception {
        // Mocking
        when(sessionManager.get()).thenReturn(session);
        when(session.openChannel("sftp")).thenReturn(channelSftp);
        doThrow(new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "File not found"))
                .when(channelSftp).lstat("/path/to/missing.txt");

        // Call the method
        boolean exists = sftpClient.fileExists(bucketName, "/path/to/missing.txt");

        // Capture Argument
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);

        // Verification
        verify(channelSftp).lstat(pathCaptor.capture());
        assertEquals("/path/to/missing.txt", pathCaptor.getValue());

        // Validate Results
        assertFalse(exists);
    }

    @Test
    void testGetFileStream() throws Exception {
        // Mocking
        InputStream mockStream = new ByteArrayInputStream("file content".getBytes());
        when(channelSftp.get("/path/to/file.txt")).thenReturn(mockStream);

        // Call the method
        InputStream stream = sftpClient.getFileStream(bucketName, "/path/to/file.txt");

        // Capture Argument
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);

        // Verification
        verify(channelSftp).get(pathCaptor.capture());
        assertEquals("/path/to/file.txt", pathCaptor.getValue());

        // Validate Results
        assertNotNull(stream);
    }

    @Test
    void testGetFileStreamThrowsError() {
        // Mocking
        try {
            when(channelSftp.get("/invalid/path.txt"))
                    .thenThrow(new SftpException(ChannelSftp.SSH_FX_FAILURE, "Error"));
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }

        // Call the method and expect exception
        SftpServiceException exception = assertThrows(SftpServiceException.class, () ->
                sftpClient.getFileStream(bucketName, "/invalid/path.txt")
        );

        // Verify Exception
        assertEquals(Error.SFTP_STREAMING_ERROR.getCode(), exception.getErrorCode());
    }

    private Vector<ChannelSftp.LsEntry> createMockedFileList() {
        Vector<ChannelSftp.LsEntry> files = new Vector<>();
        ChannelSftp.LsEntry file1 = mock(ChannelSftp.LsEntry.class);
        ChannelSftp.LsEntry file2 = mock(ChannelSftp.LsEntry.class);
        when(file1.getFilename()).thenReturn("file1.txt");
        when(file2.getFilename()).thenReturn("file2.txt");
        ArgumentCaptor<SftpATTRS> attr1 = ArgumentCaptor.forClass(SftpATTRS.class);
        ArgumentCaptor<SftpATTRS> attr2 = ArgumentCaptor.forClass(SftpATTRS.class);
        when(file1.getAttrs().isDir()).thenReturn(false);
        when(file2.getAttrs().isDir()).thenReturn(false);
        files.add(file1);
        files.add(file2);
        return files;
    }
}
