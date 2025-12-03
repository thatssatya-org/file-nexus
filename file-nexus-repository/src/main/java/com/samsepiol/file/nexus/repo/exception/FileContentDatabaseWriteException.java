package com.samsepiol.file.nexus.repo.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileContentDatabaseWriteException extends FileNexusRepositoryException {

    @Builder(access = AccessLevel.PRIVATE)
    private FileContentDatabaseWriteException() {
        super(Error.FILE_CONTENT_DB_WRITE_FAILURE);
    }

    public static FileContentDatabaseWriteException create() {
        return FileContentDatabaseWriteException.builder().build();
    }
}
