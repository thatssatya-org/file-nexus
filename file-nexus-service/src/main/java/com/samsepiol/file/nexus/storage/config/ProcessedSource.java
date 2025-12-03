package com.samsepiol.file.nexus.storage.config;

import com.samsepiol.file.nexus.storage.config.destination.AbstractDestinationConfig;
import com.samsepiol.file.nexus.storage.config.source.AbstractSourceConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ProcessedSource {
    private AbstractSourceConfig sourceConfig;
    private List<AbstractDestinationConfig> destinations;

    public List<AbstractDestinationConfig> getEnabledDestinations() {
        return destinations.stream()
                .filter(AbstractDestinationConfig::isEnabled)
                .collect(Collectors.toList());
    }

    public AbstractDestinationConfig getDestinationByName(String name) {
        return destinations.stream()
                .filter(dest -> dest.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}