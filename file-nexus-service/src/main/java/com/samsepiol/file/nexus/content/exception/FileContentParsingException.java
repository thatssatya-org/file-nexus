package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.NonNull;

public abstract class FileContentParsingException extends FileNexusException {

    protected FileContentParsingException(@NonNull Error error) {
        super(error);
    }
}
