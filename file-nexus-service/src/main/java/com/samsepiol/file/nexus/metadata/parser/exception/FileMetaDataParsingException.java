package com.samsepiol.file.nexus.metadata.parser.exception;

import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.NonNull;

public abstract class FileMetaDataParsingException extends FileNexusException {

    protected FileMetaDataParsingException(@NonNull Error error) {
        super(error);
    }
}
