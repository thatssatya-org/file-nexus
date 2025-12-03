package com.samsepiol.file.nexus.metadata.parser.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class MissingFileNameInMessageHeadersException extends FileMetaDataParsingException {

    @Builder(access = AccessLevel.PRIVATE)
    protected MissingFileNameInMessageHeadersException() {
        super(Error.MISSING_FILE_NAME_IN_MESSAGE_HEADERS);
    }

    public static MissingFileNameInMessageHeadersException create() {
        return MissingFileNameInMessageHeadersException.builder().build();
    }
}
