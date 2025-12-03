package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class UnknownFileParserConfigException extends FileNexusRuntimeException {

    @Builder(access = AccessLevel.PRIVATE)
    private UnknownFileParserConfigException() {
        super(Error.UNKNOWN_FILE_PARSER_CONFIG);
    }

    public static UnknownFileParserConfigException create() {
        return UnknownFileParserConfigException.builder().build();
    }
}
