package com.samsepiol.file.nexus.content;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.samsepiol.file.nexus.content.exception.S3Exception;
import com.samsepiol.file.nexus.models.enums.Error;
import com.samsepiol.file.nexus.transfer.storage.CloseableInputStream;
import com.samsepiol.file.nexus.transfer.storage.client.S3StorageClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3StorageClientTest {

    @Mock
    private AmazonS3 amazonS3Client;

    @InjectMocks
    private S3StorageClient s3StorageClient;

    @Test
    void testGetAllFiles_success() {
        ObjectListing mockObjectListing = mock(ObjectListing.class);
        S3ObjectSummary mockSummary1 = mock(S3ObjectSummary.class);
        S3ObjectSummary mockSummary2 = mock(S3ObjectSummary.class);
        when(mockSummary1.getKey()).thenReturn("file1.txt");
        when(mockSummary2.getKey()).thenReturn("file2.txt");
        when(mockObjectListing.getObjectSummaries()).thenReturn(Arrays.asList(mockSummary1, mockSummary2));
        when(amazonS3Client.listObjects("test-bucket")).thenReturn(mockObjectListing);

        List<String> fileNames = s3StorageClient.getAllFiles("test-bucket");

        assertNotNull(fileNames);
        assertEquals(2, fileNames.size());
        assertTrue(fileNames.contains("file1.txt"));
        assertTrue(fileNames.contains("file2.txt"));
        verify(amazonS3Client).listObjects("test-bucket");
    }

    @Test
    void testUploadFile_success() {
        String bucketName = "test-bucket";
        String filePath = "test/path";
        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
        PutObjectResult mockResult = mock(PutObjectResult.class);
        when(amazonS3Client.putObject(anyString(), anyString(), any(S3ObjectInputStream.class), any(ObjectMetadata.class)))
                .thenReturn(mockResult);
        ArgumentCaptor<String> bucketCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<ObjectMetadata> metadataCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);

        assertDoesNotThrow(() -> s3StorageClient.uploadFile(bucketName, filePath, inputStream));

        verify(amazonS3Client).putObject(bucketCaptor.capture(), keyCaptor.capture(), inputStreamCaptor.capture(), metadataCaptor.capture());
        assertEquals(bucketName, bucketCaptor.getValue());
        assertEquals(filePath, keyCaptor.getValue());
        assertEquals(inputStream, inputStreamCaptor.getValue());
        assertNotNull(metadataCaptor.getValue());
    }

    @Test
    void testUploadFile_failure() {
        InputStream mockInputStream = mock(InputStream.class);
        ObjectMetadata metadata = mock(ObjectMetadata.class);

        // Mock exception during upload
        doThrow(new RuntimeException("Upload failed")).when(amazonS3Client).putObject(anyString(), anyString(), eq(mockInputStream), eq(metadata));

        S3Exception exception = assertThrows(S3Exception.class, () -> s3StorageClient.uploadFile("test-bucket", "test/path", mockInputStream));
        assertEquals(Error.S3_FILE_UPLOAD_ERROR.getCode(), exception.getErrorCode());
    }

    @Test
    void testGetFileStream_success() {
        String bucketName = "test-bucket";
        String filePath = "test/path";

        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
        S3Object s3Object = mock(S3Object.class);
        when(amazonS3Client.getObject(bucketName, filePath)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        CloseableInputStream expected = CloseableInputStream.builder().delegate(inputStream).build();

        CloseableInputStream result = s3StorageClient.getFileStream(bucketName, filePath);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void testDeleteFile_success() {
        doNothing().when(amazonS3Client).deleteObject(anyString(), anyString());

        assertDoesNotThrow(() -> s3StorageClient.deleteFile("test-bucket", "test/path"));
    }

    @Test
    void testRenameFile_success() {

        ArgumentCaptor<String> sourceBucketCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> sourceKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> destinationBucketCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> destinationKeyCaptor = ArgumentCaptor.forClass(String.class);

        when(amazonS3Client.copyObject(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mock(CopyObjectResult.class));
        doNothing().when(amazonS3Client).deleteObject(anyString(), anyString());


        assertDoesNotThrow(() -> s3StorageClient.rename("test-bucket", "old/path", "new/path"));
        verify(amazonS3Client).copyObject(
                sourceBucketCaptor.capture(),
                sourceKeyCaptor.capture(),
                destinationBucketCaptor.capture(),
                destinationKeyCaptor.capture()
        );
        verify(amazonS3Client).deleteObject(sourceBucketCaptor.getValue(), sourceKeyCaptor.getValue());
        assertEquals("test-bucket", sourceBucketCaptor.getValue());
        assertEquals("old/path", sourceKeyCaptor.getValue());
        assertEquals("test-bucket", destinationBucketCaptor.getValue());
        assertEquals("new/path", destinationKeyCaptor.getValue());
    }

    @Test
    void testFileExists_success() {
        when(amazonS3Client.doesObjectExist("test-bucket", "test/path")).thenReturn(true);

        assertTrue(s3StorageClient.fileExists("test-bucket", "test/path"));
    }

    @Test
    void testFileExists_failure() {
        when(amazonS3Client.doesObjectExist("test-bucket", "test/path")).thenReturn(false);

        assertFalse(s3StorageClient.fileExists("test-bucket", "test/path"));
    }

    @Test
    void testDeleteFile_failure() {
        doThrow(new RuntimeException("Deletion failed")).when(amazonS3Client).deleteObject(anyString(), anyString());

        S3Exception exception = assertThrows(S3Exception.class, () -> s3StorageClient.deleteFile("test-bucket", "test/path"));
        assertEquals(Error.S3_FILE_DELETE_ERROR.getCode(), exception.getErrorCode());
    }
}
