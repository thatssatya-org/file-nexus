package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class MandatoryColumnMissingException extends FileNexusException {

    @Builder(access = AccessLevel.PRIVATE)
    private MandatoryColumnMissingException() {
        super(Error.MANDATORY_COLUMN_MISSING);
    }

    public static MandatoryColumnMissingException create() {
        return MandatoryColumnMissingException.builder().build();
    }
}
