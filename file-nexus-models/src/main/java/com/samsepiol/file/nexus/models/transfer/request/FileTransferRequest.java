package com.samsepiol.file.nexus.models.transfer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileTransferRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -3907894627602492594L;

    @NonNull
    private String sourceStore;
    @NonNull
    private String targetStore;

}
