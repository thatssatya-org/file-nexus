package com.samsepiol.file.nexus.transfer.workflow.activites.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Jacksonized
@Data
public class UploadFileActivityRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -77881122594L;
    private String remoteFilePath;
    private String sourceStore;
    private String targetStore;

}
