package com.samsepiol.file.nexus.repo.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileMetadataDatabaseReadException extends FileNexusRepositoryException {

    @Builder(access = AccessLevel.PRIVATE)
    private FileMetadataDatabaseReadException() {
        super(Error.FILE_METADATA_DB_READ_FAILURE);
    }

    public static FileMetadataDatabaseReadException create() {
        return FileMetadataDatabaseReadException.builder().build();
    }
}
