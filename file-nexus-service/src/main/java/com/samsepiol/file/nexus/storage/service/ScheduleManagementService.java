package com.samsepiol.file.nexus.storage.service;

import com.samsepiol.file.nexus.ingestion.workflow.IScheduledStorageHookWorkflow;
import com.samsepiol.file.nexus.storage.config.source.AbstractSourceConfig;
import io.temporal.api.enums.v1.ScheduleOverlapPolicy;
import io.temporal.client.schedules.Schedule;
import io.temporal.client.schedules.ScheduleActionStartWorkflow;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.client.schedules.ScheduleDescription;
import io.temporal.client.schedules.ScheduleHandle;
import io.temporal.client.schedules.ScheduleOptions;
import io.temporal.client.schedules.SchedulePolicy;
import io.temporal.client.schedules.ScheduleSpec;
import io.temporal.client.schedules.ScheduleUpdate;
import io.temporal.client.schedules.ScheduleUpdateInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.samsepiol.file.nexus.ingestion.workflow.utils.WorkflowOptionsRegistry.getIScheduledStorageHookWorkflowOptions;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleManagementService {

    private final ScheduleClient scheduleClient;

    /**
     * Get the workflow ID for a source configuration.
     *
     * @param sourceConfig The source configuration
     * @return The workflow ID
     */
    private static String getWorkflowId(AbstractSourceConfig sourceConfig) {
        return "scheduled-file-ingestion-" + sourceConfig.getName();
    }

    /**
     * Create or update a schedule for a source configuration.
     * This method checks if the source is scheduled and creates or updates the schedule accordingly.
     *
     * @param sourceConfig The source configuration
     */
    public void createOrUpdateSchedule(AbstractSourceConfig sourceConfig) {
        if (!sourceConfig.isScheduled()) {
            log.debug("Source {} is not scheduled, skipping schedule creation", sourceConfig.getName());
            return;
        }

        String scheduleId = getScheduleId(sourceConfig.getName());
        String sourceName = sourceConfig.getName();

        log.info("Schedule creation requested for source: {} with schedule: {}",
                sourceName, sourceConfig.getSchedule());

        ScheduleHandle scheduleHandle = scheduleClient.getHandle(getScheduleId(sourceName));

        try {
            if (!scheduleExists(scheduleHandle)) {
                Schedule schedule =
                        Schedule.newBuilder()
                                .setPolicy(
                                        SchedulePolicy.newBuilder()
                                                .setPauseOnFailure(false)
                                                .setCatchupWindow(Duration.ofHours(6))
                                                .setOverlap(ScheduleOverlapPolicy.SCHEDULE_OVERLAP_POLICY_TERMINATE_OTHER)
                                                .build()
                                )
                                .setAction(
                                        ScheduleActionStartWorkflow.newBuilder()
                                                .setWorkflowType(IScheduledStorageHookWorkflow.class)
                                                .setArguments(sourceName)
                                                .setOptions(
                                                        getIScheduledStorageHookWorkflowOptions(getWorkflowId(sourceConfig)))
                                                .build())
                                .setSpec(ScheduleSpec.newBuilder()
                                        .setCronExpressions(List.of(sourceConfig.getSchedule()))
                                        .build())
                                .build();
                scheduleClient.createSchedule(scheduleId, schedule,
                        ScheduleOptions.newBuilder().build());
                log.info("Successfully created schedule for source: {}", sourceName);
            } else {
                ScheduleDescription scheduleDescription = scheduleHandle.describe();
                if (!scheduleDescription.getSchedule().getSpec().getCronExpressions().contains(sourceConfig.getSchedule())) {
                    log.info("Updating schedule for source: {} to new cron expression: {}",
                            sourceName, sourceConfig.getSchedule());
                    scheduleHandle.update(
                            (ScheduleUpdateInput input) -> {
                                Schedule.Builder builder = Schedule.newBuilder(input.getDescription().getSchedule());
                                builder.setSpec(ScheduleSpec.newBuilder()
                                        .setCronExpressions(List.of(sourceConfig.getSchedule()))
                                        .build());
                                return new ScheduleUpdate(builder.build());
                            }
                    );
                    log.info("Successfully updated schedule for source: {}", sourceName);
                }
                scheduleHandle.unpause();
            }
        } catch (Exception e) {
            log.error("Error creating or updating schedule for source {}: {}", sourceName, e.getMessage(), e);
            throw new RuntimeException("Failed to create or update schedule for source: " + sourceName, e);
        }
    }

    public void pauseSchedule(AbstractSourceConfig sourceConfig) {
        String scheduleId = getScheduleId(sourceConfig.getName());
        ScheduleHandle scheduleHandle = scheduleClient.getHandle(scheduleId);

        if (scheduleExists(scheduleHandle)) {
            try {
                scheduleHandle.pause("Paused by system, as source is no longer scheduled");
                log.info("Successfully deleted schedule for source: {}", sourceConfig.getName());
            } catch (Exception e) {
                log.error("Error deleting schedule for source {}: {}", sourceConfig.getName(), e.getMessage(), e);
                throw new RuntimeException("Failed to delete schedule for source: " + sourceConfig.getName(), e);
            }
        } else {
            log.debug("No existing schedule found for source: {}, nothing to delete", sourceConfig.getName());
        }
    }

    private boolean scheduleExists(ScheduleHandle scheduleHandle) {
        try {
            scheduleHandle.describe();
            return true;
        } catch (Exception e) {
            if (e.getMessage() != null && e.getCause().getMessage().contains("NOT_FOUND")) {
                log.debug("Schedule not found for handle.");
            } else {
                log.error("Error describing schedule: {}", e.getMessage(), e);
            }
            return false;
        }
    }

    /**
     * Get the schedule ID for a source name.
     *
     * @param sourceName The source name
     * @return The schedule ID
     */
    private String getScheduleId(String sourceName) {
        return "nexus-hook-schedule-" + sourceName;
    }
}