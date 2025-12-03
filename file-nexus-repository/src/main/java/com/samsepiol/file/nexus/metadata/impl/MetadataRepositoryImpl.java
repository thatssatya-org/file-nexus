package com.samsepiol.file.nexus.metadata.impl;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.FileMetadataRepository;
import com.samsepiol.file.nexus.metadata.models.FetchMetaDataEntityRequest;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseReadException;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseUpdateException;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseWriteException;
import com.samsepiol.library.core.util.SerializationUtil;
import com.samsepiol.library.mongo.Repository;
import com.samsepiol.library.mongo.constants.EntityConstants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static com.samsepiol.file.nexus.repo.constants.RepositoryConstants.FileMetadataEntityConstants.DATE;
import static com.samsepiol.file.nexus.repo.constants.RepositoryConstants.FileMetadataEntityConstants.FILE_TYPE;

/**
 * Mongo DB based implementation of File Metadata Repository
 */
@Slf4j
@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class MetadataRepositoryImpl implements FileMetadataRepository {
    private static final String COLLECTION_NAME = "file_metadata";
    private final Repository repository;

    @Override
    public List<MetadataEntity> fetch(FetchMetaDataEntityRequest request) throws FileMetadataDatabaseReadException {
        try {
            var query = Filters.and(
                    Filters.eq(FILE_TYPE, request.getFileType()),
                    Filters.eq(DATE, request.getDate())
            );
            return repository.findAll(COLLECTION_NAME, query, MetadataEntity.class);
        } catch (MongoException ex) {
            log.error("Fetch failed with error : ", ex);
            throw FileMetadataDatabaseReadException.create();
        }
    }

    @Override
    public Optional<MetadataEntity> fetch(String fileId) {
        try {
            return Optional.ofNullable(repository.findById(COLLECTION_NAME, fileId, MetadataEntity.class));
        } catch (MongoException ex) {
            log.error("Fetch failed with error : ", ex);
            throw FileMetadataDatabaseReadException.create();
        }
    }

    @Override
    public void save(MetadataEntity metadata) throws FileMetadataDatabaseWriteException {
        try {
            repository.insert(COLLECTION_NAME, metadata, MetadataEntity.class);
        } catch (MongoException ex) {
            log.error("Insert failed with error : ", ex);
            throw FileMetadataDatabaseWriteException.create();
        }
    }

    @Override
    public void update(MetadataEntity metadata) throws FileMetadataDatabaseUpdateException {
        try {
            var map = SerializationUtil.convertToMap(metadata);
            var updates = map.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(EntityConstants.ID))
                    .map(entry -> Updates.set(entry.getKey(), entry.getValue()))
                    .toList();
            repository.updateById(COLLECTION_NAME, metadata.getId(), Updates.combine(updates));
        } catch (MongoException ex) {
            log.error("Insert failed with error : ", ex);
            throw FileMetadataDatabaseUpdateException.create();
        }
    }

    @Override
    public boolean updateStatus(@NonNull String id, @NonNull MetadataStatus status) throws FileMetadataDatabaseUpdateException {
        try {
            var update = Updates.set("status", status);
            return repository.updateById(COLLECTION_NAME, id, update);
        } catch (MongoException ex) {
            log.error("Insert failed with error : ", ex);
            throw FileMetadataDatabaseUpdateException.create();
        }
    }
}
