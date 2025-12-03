package com.samsepiol.file.nexus.repo.content;

import com.samsepiol.file.nexus.repo.content.entity.FileStateEntity;
import com.samsepiol.file.nexus.repo.content.models.request.FileStateFetchRepositoryRequest;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseReadException;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseWriteException;
import lombok.NonNull;

import java.util.Optional;

/**
 * Encapsulates Storage Layer of file states
 * Exposes CRUD operations for file state tracking
 * @author system
 */
public interface FileStateRepository {

    @NonNull
    Optional<FileStateEntity> fetch(@NonNull FileStateFetchRepositoryRequest request) throws FileContentDatabaseReadException;

    @NonNull Optional<FileStateEntity> findById(@NonNull String id) throws FileContentDatabaseReadException;

    void saveOrUpdate(@NonNull FileStateEntity request) throws FileContentDatabaseWriteException;

}
