package com.samsepiol.file.nexus.transfer.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class FileUploadException extends FileNexusRuntimeException {

    @Builder(access= AccessLevel.PRIVATE)
    private FileUploadException(Error error) {
        super(error);
    }
    public static FileUploadException create(Error error){
        return FileUploadException.builder()
                .error(error)
                .build();
    }
}
