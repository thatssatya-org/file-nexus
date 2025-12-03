package com.samsepiol.file.nexus.metadata.parser.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class MandatoryFileNameSuffixMissingException extends FileMetaDataParsingException {

    @Builder(access = AccessLevel.PRIVATE)
    protected MandatoryFileNameSuffixMissingException() {
        super(Error.MANDATORY_FILE_NAME_SUFFIX_MISSING);
    }

    public static MandatoryFileNameSuffixMissingException create() {
        return MandatoryFileNameSuffixMissingException.builder().build();
    }
}
