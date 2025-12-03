package com.samsepiol.file.nexus.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Configuration for storage hooks.
 * This class is populated from application.yaml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nexus-hook-config")
public class HookConfig {

    /**
     * List of storage configurations.
     */
    private List<Map<String, Object>> sources = new ArrayList<>();

}
