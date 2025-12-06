package com.samsepiol.file.nexus.metadata.impl;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.FileMetadataRepository;
import com.samsepiol.file.nexus.metadata.models.FetchMetaDataEntityRequest;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseReadException;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseUpdateException;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseWriteException;
import com.samsepiol.library.cache.Cache;
import com.samsepiol.library.lock.IdempotencyService;
import com.samsepiol.library.lock.exception.ParallelLockException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class CacheMetadataRepository implements FileMetadataRepository {
    private final Cache<String, MetadataEntity> cache;
    @Qualifier(FileMetadataRepository.PERSISTENT_REPOSITORY)
    private final FileMetadataRepository repository;
    private final IdempotencyService idempotencyService;

    @Override
    public List<MetadataEntity> fetch(FetchMetaDataEntityRequest request) throws FileMetadataDatabaseReadException {
        // TODO aggregate bulk fetch from cache and fetch missing from repo and cache them
        return repository.fetch(request);
    }

    @Override
    public Optional<MetadataEntity> fetch(String fileId) throws FileMetadataDatabaseReadException {
        var entity = cache.get(fileId);
        if (Objects.isNull(entity)) {
            entity = repository.fetch(fileId).orElse(null);
            tryCaching(entity);
        }
        return Optional.ofNullable(entity);
    }

    @Override
    public void save(MetadataEntity metadata) throws FileMetadataDatabaseWriteException {
        try {
            idempotencyService.execute(metadata.getId(), () -> {
                repository.save(metadata);
                return null;
            });
        } catch (ParallelLockException e) {
            throw FileMetadataDatabaseWriteException.create();
        }
    }

    @Override
    public void update(MetadataEntity metadata) throws FileMetadataDatabaseUpdateException {
        try {
            idempotencyService.execute(metadata.getId(), () -> {
                cache.delete(metadata.getId());
                repository.update(metadata);
                return null;
            });
        } catch (ParallelLockException e) {
            throw FileMetadataDatabaseUpdateException.create();
        }
    }

    @Override
    public boolean updateStatus(@NonNull String id, @NonNull MetadataStatus status) throws FileMetadataDatabaseUpdateException {
        try {
            return idempotencyService.execute(id, () -> {
                cache.delete(id);
                return repository.updateStatus(id, status);
            });
        } catch (ParallelLockException e) {
            throw FileMetadataDatabaseUpdateException.create();
        }
    }

    private void tryCaching(MetadataEntity entity) {
        try {
            if (Objects.nonNull(entity)) {
                idempotencyService.execute(entity.getId(), () -> cache.put(entity.getId(), entity));
            }
        } catch (Exception exception) {
            log.warn("[CacheMetadataRepository] Cache put failed", exception);
        }
    }
}
