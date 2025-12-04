package com.samsepiol.file.nexus.health.dependency.impl;

import com.samsepiol.file.nexus.health.dependency.DependencyHealthCheck;
import com.samsepiol.library.cache.Cache;
import com.samsepiol.library.redis.constants.BeanNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisHealthCheck implements DependencyHealthCheck {
    @Qualifier(BeanNames.REDIS_CACHE)
    private final Cache<String, String> redisCache;

    @Override
    public boolean isHealthy() {
        boolean healthy = redisCache.isHealthy();
        if (!healthy) {
            log.error("[Health Check] Redis unhealthy");
        }
        return true;
    }
}
