package com.samsepiol.file.nexus.storage.service;

import com.samsepiol.file.nexus.enums.FileStateStatus;
import com.samsepiol.file.nexus.repo.content.FileStateRepository;
import com.samsepiol.file.nexus.repo.content.entity.FileStateEntity;
import com.samsepiol.file.nexus.repo.content.models.request.FileStateFetchRepositoryRequest;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseReadException;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseWriteException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Service for managing file states in the database
 * Provides high-level operations for file state tracking
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStateService {

    private final FileStateRepository fileStateRepository;

    /**
     * Get the state of a file by state key
     *
     * @param stateKey The state key for the file
     * @return The file state status, or null if not found
     */
    public FileStateStatus getFileState(@NonNull String stateKey) {
        try {
            FileStateFetchRepositoryRequest request = FileStateFetchRepositoryRequest.builder()
                    .stateKey(stateKey)
                    .build();

            Optional<FileStateEntity> entity = fileStateRepository.fetch(request);
            return entity
                    .map(FileStateEntity::getStatus)
                    .orElse(FileStateStatus.TODO);
        } catch (FileContentDatabaseReadException e) {
            log.error("Failed to fetch file state for key: {}", stateKey, e);
            return null;
        }
    }

    /**
     * Set the state of a file
     *
     * @param filePath The file path
     * @param hookName The hook name
     * @param stateKey The state key
     * @param state    The state to set
     */
    public void setFileState(@NonNull String filePath, @NonNull String hookName,
                             @NonNull String stateKey, @NonNull FileStateStatus state) {
        try {
            Optional<FileStateEntity> fileStateEntityOptional = fileStateRepository.findById(stateKey);
            if (fileStateEntityOptional.isPresent()) {
                log.debug("File state already exists for key: {}, updating state to: {}", stateKey, state);
                FileStateEntity fileStateEntity = fileStateEntityOptional.get();
                fileStateEntity.setStatus(state);
                fileStateRepository.saveOrUpdate(fileStateEntity);
                return;
            }
            log.debug("Saving new file state for key: {}, state: {}", stateKey, state);

            long currentTime = Instant.now().toEpochMilli();
            FileStateEntity request = FileStateEntity.builder()
                    .id(stateKey)
                    .filePath(filePath)
                    .hookName(hookName)
                    .status(state)
                    .createdAt(currentTime)
                    .updatedAt(currentTime)
                    .build();
            fileStateRepository.saveOrUpdate(request);
            log.debug("File state saved: {} -> {}", stateKey, state);
        } catch (FileContentDatabaseWriteException e) {
            log.error("Failed to saveOrUpdate file state for key: {}, state: {}", stateKey, state, e);
        }
    }
}
