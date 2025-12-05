package com.samsepiol.file.nexus.transfer.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class SftpServiceException extends FileNexusRuntimeException {

    @Builder(access= AccessLevel.PRIVATE)
    private SftpServiceException(Error error) {
        super(error);
    }
    public static SftpServiceException create(Error error){
        return SftpServiceException.builder()
                .error(error)
                .build();
    }
}
