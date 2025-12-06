package com.samsepiol.file.nexus.metadata;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.models.response.FileMetadata;
import com.samsepiol.file.nexus.metadata.models.response.FileMetadatas;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataFetchServiceRequest;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataSaveRequest;
import lombok.NonNull;

/**
 * Wrapper service over repository to encapsulate business need of file metadata CRUD operations
 */
public interface FileMetadataService {

    /**
     * Fetches metadata for a fileId, throws exception if not found
     * @param fileId File Id
     * @return FileMetadata
     */
    @NonNull
    FileMetadata fetchMetadata(@NonNull String fileId);

    /**
     * Fetches all metadatas available for fileType and date
     * @param request Fetch request
     * @return FileMetadatas containing collection of file metadatas
     */
    @NonNull
    FileMetadatas fetchMetadata(@NonNull FileMetadataFetchServiceRequest request);

    /**
     * Updates status for File metadata
     * Idempotency handled based on requested status update and current metadata status
     * @param fileId File id
     * @param status Status to update
     */
    void updateStatus(@NonNull String fileId, @NonNull MetadataStatus status);

    /**
     * Saves metadata in repository by taking a lock
     * Idempotency handled based on fileId
     * @param request Save request with file metadata
     */
    void save(@NonNull FileMetadataSaveRequest request);

    /**
     * Saves metadata in repository by taking a lock
     * Updates the metadata in case it exists
     * @param request Save request with file metadata
     */
    void saveOrUpdate(@NonNull FileMetadataSaveRequest request);
}
