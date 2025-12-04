package com.samsepiol.file.nexus.exception.unchecked;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileNexusParallelLockException extends FileNexusRuntimeException {

    @Builder(access = AccessLevel.PRIVATE)
    protected FileNexusParallelLockException() {
        super(Error.PARALLEL_LOCK);
    }

    public static FileNexusParallelLockException create() {
        return FileNexusParallelLockException.builder().build();
    }
}
