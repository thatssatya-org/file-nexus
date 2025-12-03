package com.samsepiol.file.nexus.repo.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.NonNull;

public abstract class FileNexusRepositoryException extends FileNexusRuntimeException {

    protected FileNexusRepositoryException(@NonNull Error error) {
        super(error);
    }
}
