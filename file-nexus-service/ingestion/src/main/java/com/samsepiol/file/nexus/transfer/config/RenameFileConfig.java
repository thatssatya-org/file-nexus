package com.samsepiol.file.nexus.transfer.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenameFileConfig {
    private String originalPattern;
    private String filePattern;
}
