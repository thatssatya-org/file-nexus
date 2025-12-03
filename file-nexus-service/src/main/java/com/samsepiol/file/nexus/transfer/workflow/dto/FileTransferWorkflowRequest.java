package com.samsepiol.file.nexus.transfer.workflow.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class FileTransferWorkflowRequest {

    private String sourceStore;
    private String targetStore;
}
