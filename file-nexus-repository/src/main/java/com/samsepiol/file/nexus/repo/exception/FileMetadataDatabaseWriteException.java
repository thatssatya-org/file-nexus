package com.samsepiol.file.nexus.repo.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileMetadataDatabaseWriteException extends FileNexusRepositoryException {

    @Builder(access = AccessLevel.PRIVATE)
    private FileMetadataDatabaseWriteException() {
        super(Error.FILE_METADATA_DB_WRITE_FAILURE);
    }

    public static FileMetadataDatabaseWriteException create() {
        return FileMetadataDatabaseWriteException.builder().build();
    }
}
