package com.samsepiol.file.nexus.health.dependency.impl;

import com.samsepiol.file.nexus.health.dependency.DependencyHealthCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemporalServiceHealthCheck implements DependencyHealthCheck {
    // TODO
//    private final TemporalHealthCheck temporal;

    @Override
    public boolean isHealthy() {
//        boolean healthy = temporal.isHealthy();
//        if (!healthy) {
//            log.error("[Health Check] Temporal unhealthy");
//        }
        return true;
    }
}
