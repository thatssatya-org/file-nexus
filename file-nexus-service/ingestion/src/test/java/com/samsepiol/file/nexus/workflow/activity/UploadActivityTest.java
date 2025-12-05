// TODO
//package com.samsepiol.file.nexus.workflow.activity;
//
//import client.storage.transfer.com.samsepiol.file.nexus.IStorageClient;
//import com.jcraft.jsch.ChannelSftp;
//import com.samsepiol.file.nexus.content.config.SftpToS3Config;
//import enums.transfer.models.com.samsepiol.file.nexus.StorageType;
//import impl.activites.workflow.transfer.com.samsepiol.file.nexus.UploadFileActivity;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import request.activites.workflow.transfer.com.samsepiol.file.nexus.UploadFileActivityRequest;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.anyString;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class UploadActivityTest {
//
//    @Mock
//    private StorageClientFactory storageClientFactory;
//
//    @Mock
//    private SftpToS3Config config;
//
//    @Mock
//    private IStorageClient sftpClient;
//
//    @Mock
//    private IStorageClient s3Client;
//
//    @Mock
//    private ChannelSftp channel;
//
//    @InjectMocks
//    private UploadFileActivity uploadFileActivity;
//
//
//    @Test
//    void testUploadFileFromSftpToS3_Success() {
//        String bucketName = "test-bucket";
//        String remoteFilePath = "path/to/testfile.txt";
//        InputStream mockInputStream = new ByteArrayInputStream("file content".getBytes());
//
//        UploadFileActivityRequest request = UploadFileActivityRequest.builder()
//                .source(StorageType.SFTP)
//                .target(StorageType.S3)
//                .remoteFilePath(remoteFilePath)
//                .targetBucketName(bucketName)
//                .build();
//        SftpToS3Config.DefaultUploadConfig mockDefaultConfig = new SftpToS3Config.DefaultUploadConfig(bucketName, "default/path", "default/archive");
//        when(config.getDefaultUploadConfig()).thenReturn(mockDefaultConfig);
//
//        when(storageClientFactory.getStorageClient(StorageType.SFTP)).thenReturn(sftpClient);
//        when(storageClientFactory.getStorageClient(StorageType.S3)).thenReturn(s3Client);
//        when(sftpClient.fileExists(null, remoteFilePath)).thenReturn(true);
//        when(sftpClient.getChannel()).thenReturn(channel);
//        when(sftpClient.getFileStream(null, remoteFilePath, channel)).thenReturn(mockInputStream);
//
//        ArgumentCaptor<String> bucketNameCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> bucketPathCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
//
//        doNothing().when(s3Client).uploadFile(bucketNameCaptor.capture(), bucketPathCaptor.capture(), inputStreamCaptor.capture());
//
//        // Act & Assert
//        assertDoesNotThrow(() -> uploadFileActivity.upload(request));
//
//        // Verify interactions
//        verify(s3Client).uploadFile(anyString(), anyString(), any(InputStream.class));
//        verify(sftpClient).closeChannel(channel);
//
//        // Verify captured arguments
//        assertTrue(bucketName.equals(bucketNameCaptor.getValue()));
//    }
//
//
//    @Test
//    void testUploadZipFile_Success() {
//        String bucketName = "test-bucket";
//        String remoteFilePath = "path/to/testfile.zip";
//        InputStream zipContent = createMockZipInputStream(); // Utility method to mock a ZIP input stream
//
//        UploadFileActivityRequest request = UploadFileActivityRequest.builder()
//                .source(StorageType.SFTP)
//                .target(StorageType.S3)
//                .remoteFilePath(remoteFilePath)
//                .targetBucketName(bucketName)
//                .build();
//        SftpToS3Config.DefaultUploadConfig mockDefaultConfig = new SftpToS3Config.DefaultUploadConfig(bucketName, "default/path", "default/archive");
//        when(config.getDefaultUploadConfig()).thenReturn(mockDefaultConfig);
//        when(storageClientFactory.getStorageClient(StorageType.SFTP)).thenReturn(sftpClient);
//        when(storageClientFactory.getStorageClient(StorageType.S3)).thenReturn(s3Client);
//        when(sftpClient.fileExists(null, remoteFilePath)).thenReturn(true);
//        when(sftpClient.getChannel()).thenReturn(channel);
//        when(sftpClient.getFileStream(null, remoteFilePath, channel)).thenReturn(zipContent);
//        ArgumentCaptor<String> bucketNameCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> bucketPathCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
//        doNothing().when(s3Client).uploadFile(bucketNameCaptor.capture(), bucketPathCaptor.capture(), inputStreamCaptor.capture());
//
//        assertDoesNotThrow(() -> uploadFileActivity.upload(request));
//
//        verify(s3Client, times(2)).uploadFile(anyString(), anyString(), any(InputStream.class));
//        verify(sftpClient).closeChannel(channel);
//        assertTrue(bucketName.equals(bucketNameCaptor.getAllValues().get(0)));
//        assertTrue(bucketName.equals(bucketNameCaptor.getAllValues().get(1)));
//    }
//
//
//    private InputStream createMockZipInputStream() {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try (ZipOutputStream zos = new ZipOutputStream(bos)) {
//            zos.putNextEntry(new ZipEntry("file1.txt"));
//            zos.write("content1".getBytes());
//            zos.closeEntry();
//
//            zos.putNextEntry(new ZipEntry("file2.txt"));
//            zos.write("content2".getBytes());
//            zos.closeEntry();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return new ByteArrayInputStream(bos.toByteArray());
//    }
//
//}
