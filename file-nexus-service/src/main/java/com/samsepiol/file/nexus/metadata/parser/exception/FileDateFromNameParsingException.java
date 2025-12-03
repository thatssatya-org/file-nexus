package com.samsepiol.file.nexus.metadata.parser.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileDateFromNameParsingException extends FileMetaDataParsingException {

    @Builder(access = AccessLevel.PRIVATE)
    protected FileDateFromNameParsingException() {
        super(Error.FILE_DATE_FROM_NAME_PARSING_ERROR);
    }

    public static FileDateFromNameParsingException create() {
        return FileDateFromNameParsingException.builder().build();
    }
}
