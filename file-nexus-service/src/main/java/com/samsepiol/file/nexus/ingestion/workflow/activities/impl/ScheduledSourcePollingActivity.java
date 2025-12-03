package com.samsepiol.file.nexus.ingestion.workflow.activities.impl;

import com.samsepiol.file.nexus.ingestion.workflow.activities.IScheduledSourcePollingActivity;
import com.samsepiol.file.nexus.storage.models.PollResult;
import com.samsepiol.file.nexus.storage.service.StorageHookMonitoringService;
import com.samsepiol.temporal.annotations.TemporalActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@TemporalActivity
public class ScheduledSourcePollingActivity implements IScheduledSourcePollingActivity {

    private final StorageHookMonitoringService storageHookMonitoringService;

    @Override
    public PollResult pollSource(String sourceName) {
        log.info("Polling scheduled source: {}", sourceName);
        
        try {
            Optional<PollResult> pollResult = storageHookMonitoringService.pollScheduledSource(sourceName);
            if (pollResult.isEmpty()) {
                log.info("No pollResult found for scheduled source: {}", sourceName);
                return null;
            }
            log.info("Found {} pollResult for scheduled source: {}", pollResult.get(), sourceName);
            return pollResult.get();
        } catch (Exception e) {
            log.error("Error polling scheduled source: {}", sourceName, e);
            throw new RuntimeException("Failed to poll scheduled source: " + sourceName, e);
        }
    }
}
