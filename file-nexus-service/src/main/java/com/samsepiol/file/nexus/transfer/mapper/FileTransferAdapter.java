package com.samsepiol.file.nexus.transfer.mapper;

import com.samsepiol.file.nexus.models.transfer.request.FileTransferRequest;
import com.samsepiol.file.nexus.transfer.models.request.FileTransferServiceRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileTransferAdapter {

    public static FileTransferServiceRequest from(FileTransferRequest request){
        return FileTransferServiceRequest.builder()
                .sourceStore(request.getSourceStore())
                .targetStore(request.getTargetStore())
                .build();
    }
}
