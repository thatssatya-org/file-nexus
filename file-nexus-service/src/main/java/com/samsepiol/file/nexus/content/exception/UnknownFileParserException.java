package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class UnknownFileParserException extends FileNexusException {

    @Builder(access = AccessLevel.PRIVATE)
    private UnknownFileParserException() {
        super(Error.UNKNOWN_FILE_PARSER);
    }

    public static UnknownFileParserException create() {
        return UnknownFileParserException.builder().build();
    }
}
