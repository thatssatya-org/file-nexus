package com.samsepiol.file.nexus.transfer.workflow.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileTransferEventPayload  implements Serializable {

    @Serial
    private static final long serialVersionUID = -113366125999L;

    private Long initiatedAt;
    private Long completedAt;
    private String source;
    private String target;

}
