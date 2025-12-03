package com.samsepiol.file.nexus.metadata.parser.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class MissingFileNameInFilePulseStatusException extends FileMetaDataParsingException {

    @Builder(access = AccessLevel.PRIVATE)
    protected MissingFileNameInFilePulseStatusException() {
        super(Error.MISSING_FILE_NAME_IN_FILE_PULSE_STATUS);
    }

    public static MissingFileNameInFilePulseStatusException create() {
        return MissingFileNameInFilePulseStatusException.builder().build();
    }
}
