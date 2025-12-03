package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class MissingRowNumberForFileContentException extends FileContentParsingException {

    @Builder(access = AccessLevel.PRIVATE)
    private MissingRowNumberForFileContentException() {
        super(Error.MISSING_ROW_NUMBER_FOR_FILE_CONTENT);
    }

    public static MissingRowNumberForFileContentException create() {
        return MissingRowNumberForFileContentException.builder().build();
    }
}
