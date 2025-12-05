package com.samsepiol.file.nexus.health.service.dependency.impl;

import com.samsepiol.file.nexus.health.service.dependency.DependencyHealthCheck;
import com.samsepiol.file.nexus.health.service.dependency.models.enums.DependencyType;
import io.temporal.api.workflowservice.v1.GetSystemInfoRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@ConditionalOnProperty("temporal-config.host")
@RequiredArgsConstructor
public class TemporalServiceHealthCheck implements DependencyHealthCheck {
    private final WorkflowServiceStubs service;

    @Override
    public @NonNull Supplier<Boolean> healthCheck() {
        return () -> {
            var systemInfo = service.blockingStub().getSystemInfo(GetSystemInfoRequest.getDefaultInstance());
            return systemInfo.isInitialized();
        };
    }

    @Override
    public @NonNull DependencyType getType() {
        return DependencyType.TEMPORAL;
    }
}
