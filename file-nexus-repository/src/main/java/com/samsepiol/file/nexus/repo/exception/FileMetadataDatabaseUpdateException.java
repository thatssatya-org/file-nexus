package com.samsepiol.file.nexus.repo.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileMetadataDatabaseUpdateException extends FileNexusRepositoryException {

    @Builder(access = AccessLevel.PRIVATE)
    private FileMetadataDatabaseUpdateException() {
        super(Error.FILE_METADATA_DB_UPDATE_FAILURE);
    }

    public static FileMetadataDatabaseUpdateException create() {
        return FileMetadataDatabaseUpdateException.builder().build();
    }
}
