package com.samsepiol.file.nexus.repo.content;

import com.samsepiol.file.nexus.repo.content.entity.FileContent;
import com.samsepiol.file.nexus.repo.content.models.request.FileContentFetchRepositoryRequest;
import com.samsepiol.file.nexus.repo.content.models.request.FileContentSaveRepositoryRequest;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseReadException;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseWriteException;
import lombok.NonNull;

import java.util.List;

/**
 * Encapsulates Storage Layer of file contents
 * Exposes CRUD operations
 * @author satyajitroy
 */
public interface FileContentRepository {

    @NonNull
    List<FileContent> fetch(@NonNull FileContentFetchRepositoryRequest request) throws FileContentDatabaseReadException;

    @NonNull
    List<FileContent> save(@NonNull FileContentSaveRepositoryRequest request) throws FileContentDatabaseWriteException;

}
