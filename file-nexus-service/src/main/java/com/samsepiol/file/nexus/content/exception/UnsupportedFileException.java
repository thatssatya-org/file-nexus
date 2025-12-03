package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class UnsupportedFileException extends FileNexusException {

    @Builder(access = AccessLevel.PRIVATE)
    private UnsupportedFileException() {
        super(Error.UNSUPPORTED_FILE);
    }

    public static UnsupportedFileException create() {
        return UnsupportedFileException.builder().build();
    }
}
