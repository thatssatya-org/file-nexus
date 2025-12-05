package com.samsepiol.file.nexus.storage.config;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ProcessedHookConfiguration {
    private final List<ProcessedSource> sources;
    private final Map<String, ProcessedSource> sourceMap;

    public ProcessedHookConfiguration(List<ProcessedSource> sources) {
        this.sources = sources;
        this.sourceMap = sources.stream()
                .collect(Collectors.toMap(source -> source.getSourceConfig().getName(), source -> source));
    }

    public ProcessedSource getSourceByName(String name) {
        return sourceMap.get(name);
    }
}