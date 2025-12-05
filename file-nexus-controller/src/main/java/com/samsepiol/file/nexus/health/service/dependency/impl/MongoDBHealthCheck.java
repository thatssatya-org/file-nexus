package com.samsepiol.file.nexus.health.service.dependency.impl;

import com.samsepiol.file.nexus.health.service.dependency.DependencyHealthCheck;
import com.samsepiol.library.mongo.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(Repository.class)
@RequiredArgsConstructor
@Slf4j
public class MongoDBHealthCheck implements DependencyHealthCheck {
    private final Repository repository;

    @Override
    public boolean isHealthy() {
        boolean healthy = repository.isHealthy();
        if (!healthy) {
            log.error("[Health Check] MongoDB unhealthy");
        }
        return healthy;
    }
}
