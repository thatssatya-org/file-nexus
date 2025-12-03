package com.samsepiol.file.nexus.models.transfer.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class FileTransferResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1789324234099L;

    private String workflowId;
}
