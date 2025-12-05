package com.samsepiol.file.nexus.transfer.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class SftpConnectionException extends FileNexusRuntimeException {

    @Builder(access= AccessLevel.PRIVATE)
    private SftpConnectionException(Error error) {
        super(error);
    }
    public static SftpConnectionException create(Error error){
        return SftpConnectionException.builder()
                .error(error)
                .build();
    }

}
