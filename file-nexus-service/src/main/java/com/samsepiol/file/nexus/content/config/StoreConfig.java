package com.samsepiol.file.nexus.content.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "store-config")
@Data
@NoArgsConstructor
public class StoreConfig {

    private List<StoreConfigEntry> storeConfigEntry;

}
