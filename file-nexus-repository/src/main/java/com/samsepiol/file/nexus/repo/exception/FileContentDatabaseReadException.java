package com.samsepiol.file.nexus.repo.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileContentDatabaseReadException extends FileNexusRepositoryException {

    @Builder(access = AccessLevel.PRIVATE)
    private FileContentDatabaseReadException() {
        super(Error.FILE_CONTENT_DB_READ_FAILURE);
    }

    public static FileContentDatabaseReadException create() {
        return FileContentDatabaseReadException.builder().build();
    }
}
