package com.samsepiol.file.nexus.metadata;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.models.FetchMetaDataEntityRequest;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseReadException;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseUpdateException;
import com.samsepiol.file.nexus.repo.exception.FileMetadataDatabaseWriteException;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Encapsulates Storage Layer of file metadata
 * Exposes CRUD operations
 */
public interface FileMetadataRepository {
    List<MetadataEntity> fetch(FetchMetaDataEntityRequest request) throws FileMetadataDatabaseReadException;

    Optional<MetadataEntity> fetch(String fileId) throws FileMetadataDatabaseReadException;

    void save(MetadataEntity metadata) throws FileMetadataDatabaseWriteException;

    void update(MetadataEntity metadata) throws FileMetadataDatabaseUpdateException;

    boolean updateStatus(@NonNull String id, @NonNull MetadataStatus status) throws FileMetadataDatabaseUpdateException;
}
