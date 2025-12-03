package com.samsepiol.file.nexus.health.dependency.impl;

import com.samsepiol.file.nexus.health.dependency.DependencyHealthCheck;
import com.samsepiol.mongo.helper.IMongoDbHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongoDBHealthCheck implements DependencyHealthCheck {
    private final IMongoDbHelper mongoDb;

    @Override
    public boolean isHealthy() {
        boolean healthy = mongoDb.isHealthy();
        if (!healthy) {
            log.error("[Health Check] MongoDB unhealthy");
        }

        return healthy;
    }
}
