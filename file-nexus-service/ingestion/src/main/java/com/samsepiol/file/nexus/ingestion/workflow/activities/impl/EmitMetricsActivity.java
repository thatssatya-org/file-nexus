package com.samsepiol.file.nexus.ingestion.workflow.activities.impl;


import com.samsepiol.file.nexus.ingestion.workflow.activities.IEmitMetricsActivity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of the emit metrics activity.
 */
@Slf4j
@Component
@RequiredArgsConstructor

public class EmitMetricsActivity implements IEmitMetricsActivity {
    public static final String FILE_NEXUS_NO_WORKFLOWS_STARTED = "FILE_NEXUS_NO_WORKFLOWS_STARTED";
    
    private static final String METRIC_PREFIX = "file.nexus.scheduled.source.";
    
    private static final String FILE_NEXUS_FILE_NOT_FOUND_EVENT = "FILE_NEXUS_FILE_NOT_FOUND";
    private static final String FILE_NEXUS_FILES_FOUND_EVENT = "FILE_NEXUS_FILES_FOUND";

    @Override
    public void emitNoFilesFoundMetric(String sourceName, long timestamp) {
        log.info("METRIC: No files found for scheduled source: {} at timestamp: {}", sourceName, timestamp);
        try {
//            metricHelper.incrementCounter(getMetricName("no.files"), Map.of("source", sourceName));
            // TODOeventHelper.publishSync(FILE_NEXUS_FILE_NOT_FOUND_EVENT,
//                    Map.of("source", sourceName, "timestamp", timestamp));
        } catch (Exception e) {
            log.error("Failed to emit no-files-found metric for source: {}", sourceName, e);
        }
    }

    @Override
    public void emitFilesFoundMetric(String sourceName, int fileCount, long timestamp) {
        log.info("METRIC: Found {} files for scheduled source: {} at timestamp: {}", fileCount, sourceName, timestamp);
        try {
//            metricHelper.incrementCounter(getMetricName("files.found"), Map.of("source", sourceName), fileCount);
            // TODOeventHelper.publish(FILE_NEXUS_FILES_FOUND_EVENT,
//                    Map.of("source", sourceName, "fileCount", fileCount, "timestamp", timestamp));
        } catch (Exception e) {
            log.error("Failed to emit files-found metric for source: {}", sourceName, e);
        }
    }

    @Override
    public void emmitNoWorkflowsStartedMetric(String sourceName, int size, long startTime) {
        log.info("METRIC: No workflows started for source: {} with size: {} at start time: {}",
                sourceName, size, startTime);
        try {
//            metricHelper.incrementCounter(getMetricName("no.workflows.started"),
//                    Map.of("source", sourceName));
            // TODOeventHelper.publish(FILE_NEXUS_NO_WORKFLOWS_STARTED,
//                    Map.of("source", sourceName, "size", size, "startTime", startTime));
        } catch (Exception e) {
            log.error("Failed to emit no-workflows-started metric for source: {}", sourceName, e);
        }
    }

    private String getMetricName(String metricName) {
        return METRIC_PREFIX + "." + metricName;
    }
}
