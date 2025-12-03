package com.samsepiol.file.nexus.health.impl;

import com.samsepiol.file.nexus.health.HealthCheckService;
import com.samsepiol.file.nexus.health.dependency.DependencyHealthCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultHealthCheckService implements HealthCheckService {
    private final List<DependencyHealthCheck> dependencyHealthChecks;

    @Override
    public boolean isHealthy() {
        return dependencyHealthChecks.stream().allMatch(DependencyHealthCheck::isHealthy);
    }
}
