package com.samsepiol.file.nexus.storage.config;

import com.samsepiol.file.nexus.storage.config.destination.AbstractDestinationConfig;
import com.samsepiol.file.nexus.storage.config.source.AbstractSourceConfig;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HookConfigurationProvider implements InitializingBean {

    public static final String DESTINATIONS_FIELD = "destinations";
    private final HookConfig rawConfiguration;
    private final ConfigurationFactory factory;
    @Getter
    private ProcessedHookConfiguration processedConfiguration;

    public HookConfigurationProvider(HookConfig rawConfiguration,
                                     ConfigurationFactory factory) {
        this.rawConfiguration = rawConfiguration;
        this.factory = factory;
    }

    @Override
    public void afterPropertiesSet() {
        processConfiguration();
    }

    private void processConfiguration() {
        List<ProcessedSource> processedSources = new ArrayList<>();

        for (Map<String, Object> sourceMap : rawConfiguration.getSources()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> destMaps = (Map<String, Object>) sourceMap.get(DESTINATIONS_FIELD);
            sourceMap.remove(DESTINATIONS_FIELD); // Remove destinations from sourceMap to avoid duplication

            AbstractSourceConfig sourceConfig = factory.createSourceConfig(sourceMap);
            // Process destinations
            List<AbstractDestinationConfig> destinations = destMaps != null ?
                    factory.createDestinationConfigs(destMaps) : Collections.emptyList();

            processedSources.add(new ProcessedSource(sourceConfig, destinations));
        }

        this.processedConfiguration = new ProcessedHookConfiguration(processedSources);
    }
}