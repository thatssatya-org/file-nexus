package com.samsepiol.file.nexus.transfer.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class UnImplementedException extends FileNexusRuntimeException {

    @Builder(access= AccessLevel.PRIVATE)
    private UnImplementedException(Error error) {
        super(error);
    }
    public static UnImplementedException create(Error error){
        return UnImplementedException.builder()
                .error(error)
                .build();
    }
}
