package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class TudfFileConfigLoadingException extends FileNexusRuntimeException {

    @Builder(access = AccessLevel.PRIVATE)
    private TudfFileConfigLoadingException() {
        super(Error.DISCARDED_FILE_REQUESTED_ERROR);
    }

    public static TudfFileConfigLoadingException create() {
        return TudfFileConfigLoadingException.builder().build();
    }
}
