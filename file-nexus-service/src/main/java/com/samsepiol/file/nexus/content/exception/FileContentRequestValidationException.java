package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileContentRequestValidationException extends FileNexusException {

    @Builder(access = AccessLevel.PRIVATE)
    private FileContentRequestValidationException(Error error) {
        super(error);
    }

    public static FileContentRequestValidationException create(Error error) {
        return FileContentRequestValidationException.builder()
                .error(error)
                .build();
    }
}
