package com.samsepiol.file.nexus.health.dependency.impl;

import com.samsepiol.file.nexus.health.dependency.DependencyHealthCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisHealthCheck implements DependencyHealthCheck {
    // TODO
//    private final RedisClient redisClient;

    @Override
    public boolean isHealthy() {
//        boolean healthy = redisClient.isHealthy();
//        if (!healthy) {
//            log.error("[Health Check] Redis unhealthy");
//        }

        return true;
    }
}
