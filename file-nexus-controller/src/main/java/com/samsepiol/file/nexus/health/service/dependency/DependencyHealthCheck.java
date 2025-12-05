package com.samsepiol.file.nexus.health.service.dependency;

// TODO move to library, introduce dependency tiers for reporting failure
public interface DependencyHealthCheck {
    boolean isHealthy();
}
