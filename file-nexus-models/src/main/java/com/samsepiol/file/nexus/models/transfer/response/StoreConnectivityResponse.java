package com.samsepiol.file.nexus.models.transfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreConnectivityResponse implements Serializable {

    private String message;
    private List<String> fileNames;
}
