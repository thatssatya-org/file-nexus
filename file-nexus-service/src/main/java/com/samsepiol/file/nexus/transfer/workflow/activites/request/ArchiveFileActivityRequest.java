package com.samsepiol.file.nexus.transfer.workflow.activites.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveFileActivityRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -9012789912594L;
    private String remoteFilePath;
    private String sourceStore;
    private String targetStore;
    private String bucketName;
}
