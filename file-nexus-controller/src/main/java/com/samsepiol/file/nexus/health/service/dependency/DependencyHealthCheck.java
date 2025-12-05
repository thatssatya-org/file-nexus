package com.samsepiol.file.nexus.health.service.dependency;

import com.samsepiol.file.nexus.health.service.dependency.models.enums.DependencyType;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

// TODO move to library, introduce dependency tiers for reporting failure
public interface DependencyHealthCheck {
    Logger LOGGER = LoggerFactory.getLogger(DependencyHealthCheck.class);

    default boolean isHealthy() {
        try {
            return healthCheck().get();
        } catch (Exception e) {
            LOGGER.error("[Health Check] {} unhealthy", getType());
            return Boolean.FALSE;
        }
    }

    @NonNull
    Supplier<Boolean> healthCheck();

    @NonNull
    DependencyType getType();
}
