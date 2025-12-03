package com.samsepiol.file.nexus.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDetails {

    private String fileKey;
    private String filePath;
    private long size;
}
