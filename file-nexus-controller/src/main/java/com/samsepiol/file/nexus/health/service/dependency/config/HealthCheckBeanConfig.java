package com.samsepiol.file.nexus.health.service.dependency.config;

import com.samsepiol.file.nexus.health.service.dependency.DependencyHealthCheck;
import com.samsepiol.file.nexus.health.service.dependency.models.enums.DependencyType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class HealthCheckBeanConfig {

    @Bean
    public Map<DependencyType, DependencyHealthCheck> dependencyHealthCheckMap(List<DependencyHealthCheck> healthChecks) {
        return healthChecks.stream().collect(Collectors.toUnmodifiableMap(DependencyHealthCheck::getType, Function.identity()));
    }
}
