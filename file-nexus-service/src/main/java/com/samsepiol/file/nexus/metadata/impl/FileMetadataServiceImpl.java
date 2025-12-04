package com.samsepiol.file.nexus.metadata.impl;

import com.samsepiol.file.nexus.content.exception.FileNotFoundException;
import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.exception.unchecked.FileNexusParallelLockException;
import com.samsepiol.file.nexus.metadata.FileMetadataRepository;
import com.samsepiol.file.nexus.metadata.FileMetadataService;
import com.samsepiol.file.nexus.metadata.mapper.MetadataAdapter;
import com.samsepiol.file.nexus.metadata.message.handler.models.response.FileMetadata;
import com.samsepiol.file.nexus.metadata.message.handler.models.response.FileMetadatas;
import com.samsepiol.file.nexus.metadata.models.FetchMetaDataEntityRequest;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataFetchServiceRequest;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataSaveRequest;
import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import com.samsepiol.library.lock.IdempotencyService;
import com.samsepiol.library.lock.exception.ParallelLockException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.samsepiol.file.nexus.enums.MetadataStatus.PENDING;
import static com.samsepiol.file.nexus.metadata.mapper.MetadataAdapter.fileMetadataListFromEntityList;
import static com.samsepiol.file.nexus.utils.ClassUtils.emptyConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataServiceImpl implements FileMetadataService {
    private final FileMetadataRepository metadataRepo;
    private final IdempotencyService idempotencyService;

    @Override
    public @NonNull FileMetadata fetchMetadata(@NonNull String fileId) {
        return fetchMetadataEntity(fileId)
                .map(MetadataAdapter::fileMetadataFromEntity)
                .orElseThrow(FileNotFoundException::create);
    }

    @Override
    public @NonNull FileMetadatas fetchMetadata(@NonNull FileMetadataFetchServiceRequest request) {
        FetchMetaDataEntityRequest entityRequest = FetchMetaDataEntityRequest.builder()
                .fileType(request.getFileType())
                .date(request.getDate())
                .build();
        List<MetadataEntity> entities = metadataRepo.fetch(entityRequest);
        return FileMetadatas.from(fileMetadataListFromEntityList(entities));
    }

    @Override
    public void updateStatus(@NonNull String fileId, @NonNull MetadataStatus status) {
        MetadataEntity metadataEntity = fetchMetadataEntity(fileId).orElseThrow(FileNotFoundException::create);
        updateEntity(metadataEntity, status);
    }

    @Override
    public void save(@NonNull FileMetadataSaveRequest request) {
        fetchMetadataEntity(request.getParsedFileMetaData().getFileId()).ifPresentOrElse(
                emptyConsumer(),
                () -> saveWithLock(request));
    }

    @Override
    public void saveOrUpdate(@NonNull FileMetadataSaveRequest request) {
        fetchMetadataEntity(request.getParsedFileMetaData().getFileId()).ifPresentOrElse(
                entity -> updateEntity(entity, request),
                () -> saveWithLock(request));
    }

    private void saveWithLock(FileMetadataSaveRequest request) {
        try {
            idempotencyService.execute(
                    request.getParsedFileMetaData().getFileId(),
                    () -> {
                        var metadataEntity = createMetadataEntity(request.getParsedFileMetaData(), request.getStatus());
                        metadataRepo.save(metadataEntity);
                        return null;
                    });
        } catch (ParallelLockException e) {
            throw FileNexusParallelLockException.create();
        }

    }

    private void updateEntity(MetadataEntity entity,
                              FileMetadataSaveRequest request) {

        if (entity.getStatus() != request.getStatus() && isMetadataStatusPending(entity)) {
            var metadataEntity = createMetadataEntity(request.getParsedFileMetaData(), request.getStatus());
            metadataRepo.update(metadataEntity);
        }
    }

    private static boolean isMetadataStatusPending(MetadataEntity entity) {
        return entity.getStatus() == PENDING;
    }

    private void updateEntity(MetadataEntity metadataEntity, MetadataStatus status) {
        if (metadataEntity.getStatus() != status) {
            metadataRepo.updateStatus(metadataEntity.getId(), status);
        }
    }

    private Optional<MetadataEntity> fetchMetadataEntity(String fileId) {
        return metadataRepo.fetch(fileId);
    }

    private MetadataEntity createMetadataEntity(ParsedFileMetaData parsedFileMetaData,
                                                MetadataStatus status) {
        var createdAt = Objects.requireNonNullElse(parsedFileMetaData.getCreatedAt(), DateTimeUtils.currentTimeInEpoch());
        return MetadataEntity.builder()
                .id(parsedFileMetaData.getFileId())// _0 append
                .fileType(parsedFileMetaData.getFileType())
                .fileName(parsedFileMetaData.getName())
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .date(parsedFileMetaData.getFileDate())
                .status(status)
                .build();
    }
}
