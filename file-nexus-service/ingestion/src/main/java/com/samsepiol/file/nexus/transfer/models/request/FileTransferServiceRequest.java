package com.samsepiol.file.nexus.transfer.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileTransferServiceRequest {

    private String sourceStore;
    private String targetStore;
}
