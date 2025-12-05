package com.samsepiol.file.nexus.ingestion.workflow.activities.impl;

import com.samsepiol.file.nexus.ingestion.workflow.activities.ITriggerFileIngestionActivity;
import com.samsepiol.file.nexus.storage.models.PollResult;
import com.samsepiol.file.nexus.storage.service.StorageHookMonitoringService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of the trigger file ingestion activity.
 */
@Slf4j
@Component
@RequiredArgsConstructor

public class TriggerFileIngestionActivity implements ITriggerFileIngestionActivity {

    private final StorageHookMonitoringService storageHookMonitoringService;

    @Override
    public List<String> triggerFileIngestionWorkflows(String sourceName, PollResult pollResult) {
        log.info("Triggering file ingestion workflows for {} files from source: {}", pollResult.files().size(), sourceName);
        
        List<String> workflowIds = storageHookMonitoringService.processNewFiles(sourceName, pollResult, true);

        if (workflowIds.isEmpty()) {
            log.warn("No workflows were triggered for source: {}", sourceName);
            return workflowIds;
        }

        log.info("Successfully triggered {} file ingestion workflows for source: {}", workflowIds.size(), sourceName);
        return workflowIds;
    }
}
