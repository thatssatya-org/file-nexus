package com.samsepiol.file.nexus.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "cache-config")
public class CacheConfig {

    private StatementFile statementFile;

    @Data
    public static class StatementFile {
        private int ttl;
        private final TimeUnit timeUnit = TimeUnit.SECONDS;
    }
}
