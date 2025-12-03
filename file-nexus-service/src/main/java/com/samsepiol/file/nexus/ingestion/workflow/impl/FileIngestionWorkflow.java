package com.samsepiol.file.nexus.ingestion.workflow.impl;

import com.samsepiol.file.nexus.ingestion.workflow.IFileIngestionWorkflow;
import com.samsepiol.file.nexus.ingestion.workflow.activities.ISendToDestinationActivity;
import com.samsepiol.file.nexus.ingestion.workflow.dto.FileIngestionWorkflowRequest;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.samsepiol.file.nexus.ingestion.workflow.activities.ISendToDestinationActivity.ACTIVITY_OPTIONS;

/**
 * Implementation of the file ingestion workflow.
 * This workflow is triggered when new files are detected by storage hooks.
 * It supports a fan-out pattern for sending files to multiple destinations concurrently.
 */
@Slf4j
public class FileIngestionWorkflow implements IFileIngestionWorkflow {

    private final ISendToDestinationActivity sendToDestinationActivity = 
            Workflow.newActivityStub(ISendToDestinationActivity.class, ACTIVITY_OPTIONS);

    @Override
    public void processFile(FileIngestionWorkflowRequest request) {
        sendToDestinationActivity.markFileAsScheduled(request);

        List<Promise<Boolean>> destinationPromises = new ArrayList<>();
        for (String destination : request.getDestinations()) {
            Promise<Boolean> promise = Async.function(sendToDestinationActivity::sendToDestinationWithConfig,
                request.getSourceName(),
                request.getFileInfo(),
                destination
            );
            destinationPromises.add(promise);
        }
        Promise.allOf(destinationPromises).get();
        sendToDestinationActivity.markFileAsProcessed(request);
    }
}
