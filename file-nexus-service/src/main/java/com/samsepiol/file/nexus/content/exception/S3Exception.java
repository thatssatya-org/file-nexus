package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class S3Exception extends FileNexusRuntimeException {

    @Builder(access= AccessLevel.PRIVATE)
    private S3Exception(Error error) {
        super(error);
    }
    public static S3Exception create(Error error){
        return S3Exception.builder()
                .error(error)
                .build();
    }
}
