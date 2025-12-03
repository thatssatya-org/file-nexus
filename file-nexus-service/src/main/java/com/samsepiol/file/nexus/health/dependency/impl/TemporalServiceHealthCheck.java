package com.samsepiol.file.nexus.health.dependency.impl;

import com.samsepiol.file.nexus.health.dependency.DependencyHealthCheck;
import io.temporal.api.workflowservice.v1.GetSystemInfoRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(WorkflowServiceStubs.class)
@RequiredArgsConstructor
@Slf4j
public class TemporalServiceHealthCheck implements DependencyHealthCheck {
    private final WorkflowServiceStubs service;

    @Override
    public boolean isHealthy() {
        try {
            var systemInfo = service.blockingStub().getSystemInfo(GetSystemInfoRequest.getDefaultInstance());
            return systemInfo.isInitialized();
        } catch (Exception e) {
            log.error("[Health Check] Temporal unhealthy");
            return Boolean.FALSE;
        }
    }
}
