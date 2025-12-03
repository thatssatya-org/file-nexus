package com.samsepiol.file.nexus.content.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TudfFileParserLoadConfig {
    private Integer recordIdentifierIndex;
    private List<RecordTypeToFileMapping> identifiersToFileName;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecordTypeToFileMapping {
        private Character identifier;
        private String filePath;
    }
}
