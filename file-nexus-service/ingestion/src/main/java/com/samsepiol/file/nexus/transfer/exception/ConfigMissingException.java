package com.samsepiol.file.nexus.transfer.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class ConfigMissingException extends FileNexusRuntimeException {

    @Builder(access= AccessLevel.PRIVATE)
    private ConfigMissingException(Error error) {
        super(error);
    }
    public static ConfigMissingException create(Error error){
        return ConfigMissingException.builder()
                .error(error)
                .build();
    }
}
