package com.samsepiol.file.nexus.content;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.FileMetadataRepository;
import com.samsepiol.file.nexus.metadata.impl.FileMetadataServiceImpl;
import com.samsepiol.file.nexus.metadata.models.FetchMetaDataEntityRequest;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataFetchServiceRequest;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataSaveRequest;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import com.samsepiol.library.lock.IdempotencyService;
import com.samsepiol.library.lock.exception.ParallelLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Supplier;

import static com.samsepiol.file.nexus.content.MetadataTestUtil.createTestMetadataEntity;
import static com.samsepiol.file.nexus.content.MetadataTestUtil.createTestMetadataEntityList;
import static com.samsepiol.file.nexus.content.MetadataTestUtil.createTestParsedFileMetadata;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileHandlerDataServiceUnitTest {
    @Mock
    private FileMetadataRepository metadataRepo;

    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private FileMetadataServiceImpl fileHandlerDataService;

    @Test
    void fetchMetadata_byFileId_Test(){
        String fileId = "STATEMENT_20241007";
        when(metadataRepo.fetch(fileId)).thenReturn(Optional.ofNullable(createTestMetadataEntity()));
        fileHandlerDataService.fetchMetadata(fileId);
        verify(metadataRepo, Mockito.times(1)).fetch(fileId);
    }

    @Test
    void fetchMetadataByRequest_success_test(){
        FileMetadataFetchServiceRequest serviceRequest = FileMetadataFetchServiceRequest.builder()
                .date("20241007")
                .fileType("STATEMENT")
                .build();

        FetchMetaDataEntityRequest entityRequest = FetchMetaDataEntityRequest.builder()
                .date("20241007")
                .fileType("STATEMENT")
                .build();
        when(metadataRepo.fetch(entityRequest)).thenReturn(createTestMetadataEntityList());
        fileHandlerDataService.fetchMetadata(serviceRequest);
        verify(metadataRepo, Mockito.times(1)).fetch(entityRequest);
    }

    @Test
    void updateStatus_success_test(){
        String fileId = "STATEMENT_20241007";
        doNothing().when(metadataRepo).update(any(MetadataEntity.class));
        when(metadataRepo.fetch(fileId)).thenReturn(Optional.ofNullable(createTestMetadataEntity()));

        fileHandlerDataService.updateStatus(fileId, MetadataStatus.COMPLETED);
        verify(metadataRepo, Mockito.times(1)).update(any(MetadataEntity.class));
        verify(metadataRepo, Mockito.times(1)).fetch(fileId);
    }

    @Test
    void saveMetadata_success_test() throws ParallelLockException {
        String fileId = "STATEMENT_20241007";
        doNothing().when(metadataRepo).save(any(MetadataEntity.class));
        FileMetadataSaveRequest request = FileMetadataSaveRequest.builder()
                .parsedFileMetaData(createTestParsedFileMetadata())
                .status(MetadataStatus.COMPLETED)
                .build();

        var runnableArgumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        fileHandlerDataService.save(request);

        verify(idempotencyService, Mockito.times(1))
                .execute(eq(fileId), runnableArgumentCaptor.capture());

        runnableArgumentCaptor.getValue().get();
        verify(metadataRepo, Mockito.times(1)).save(any(MetadataEntity.class));
        verify(metadataRepo, Mockito.times(1)).fetch(fileId);

    }

    @Test
    void saveOrUpdateByFileId_success_test(){
        String fileId = "STATEMENT_20241007";
        when(metadataRepo.fetch(fileId)).thenReturn(Optional.ofNullable(createTestMetadataEntity()));

        FileMetadataSaveRequest request = FileMetadataSaveRequest.builder()
                .parsedFileMetaData(createTestParsedFileMetadata())
                .status(MetadataStatus.PENDING)
                .build();

        fileHandlerDataService.saveOrUpdate(request);
        verify(metadataRepo, Mockito.times(1)).fetch(fileId);

    }

}
