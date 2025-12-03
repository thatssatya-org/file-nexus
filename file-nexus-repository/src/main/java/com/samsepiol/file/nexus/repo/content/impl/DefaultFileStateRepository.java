package com.samsepiol.file.nexus.repo.content.impl;

import com.mongodb.MongoException;
import com.mongodb.client.model.Updates;
import com.samsepiol.file.nexus.repo.content.FileStateRepository;
import com.samsepiol.file.nexus.repo.content.entity.FileStateEntity;
import com.samsepiol.file.nexus.repo.content.models.request.FileStateFetchRepositoryRequest;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseReadException;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseWriteException;
import com.samsepiol.library.core.util.SerializationUtil;
import com.samsepiol.library.mongo.Repository;
import com.samsepiol.library.mongo.constants.EntityConstants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

/**
 * Mongo DB based implementation of File State Repository
 *
 * @author system
 */
@Slf4j
@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class DefaultFileStateRepository implements FileStateRepository {
    private static final String COLLECTION_NAME = "file_states";
    private final Repository repository;

    @Override
    public @NonNull Optional<FileStateEntity> fetch(@NonNull FileStateFetchRepositoryRequest request) throws FileContentDatabaseReadException {
        try {
            FileStateEntity result = repository.findById(COLLECTION_NAME, request.getStateKey(), FileStateEntity.class);
            return Optional.ofNullable(result);
        } catch (MongoException exception) {
            log.error("File state DB read failure: ", exception);
            throw FileContentDatabaseReadException.create();
        }
    }

    @Override
    public @NonNull Optional<FileStateEntity> findById(@NonNull String id) throws FileContentDatabaseReadException {
        try {
            FileStateEntity result = repository.findById(COLLECTION_NAME, id, FileStateEntity.class);
            return Optional.ofNullable(result);
        } catch (MongoException exception) {
            log.error("File state DB read failure: ", exception);
            throw FileContentDatabaseReadException.create();
        }
    }

    @Override
    public void saveOrUpdate(@NonNull FileStateEntity request) throws FileContentDatabaseWriteException {
        try {
            request.setUpdatedAt(Instant.now().toEpochMilli());
            var map = SerializationUtil.convertToMap(request);
            var updates = map.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(EntityConstants.ID))
                    .map(entry -> Updates.set(entry.getKey(), entry.getValue()))
                    .toList();
            repository.upsertById(COLLECTION_NAME, request.getId(), Updates.combine(updates));
        } catch (MongoException exception) {
            log.error("File state DB write failure: ", exception);
            throw FileContentDatabaseWriteException.create();
        }
    }

}
