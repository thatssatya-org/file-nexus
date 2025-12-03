package com.samsepiol.file.nexus.content.data.parser.exception;

import com.samsepiol.file.nexus.content.exception.FileContentParsingException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class TudfFileContentParsingException extends FileContentParsingException {

    @Builder(access = AccessLevel.PRIVATE)
    private TudfFileContentParsingException() {
        super(Error.TUDF_FILE_CONTENT_PARSING_FAILURE);
    }

    public static TudfFileContentParsingException create() {
        return TudfFileContentParsingException.builder().build();
    }
}
