package com.samsepiol.file.nexus.health.service.impl;

import com.samsepiol.file.nexus.health.service.HealthCheckService;
import com.samsepiol.file.nexus.health.service.dependency.DependencyHealthCheck;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultHealthCheckService implements HealthCheckService {
    private final List<DependencyHealthCheck> dependencyHealthChecks;

    @PostConstruct
    public void init() {
        isHealthy();
    }

    @Override
    public boolean isHealthy() {
        return dependencyHealthChecks.stream()
                .allMatch(dependencyHealthCheck -> {
                    log.info("[Health Check] - {} - {}", dependencyHealthCheck.getClass().getSimpleName(), dependencyHealthCheck.isHealthy());
                    return dependencyHealthCheck.isHealthy();
                });
    }
}
