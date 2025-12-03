package com.samsepiol.file.nexus.metadata.message.handler.impl;

import com.samsepiol.file.nexus.content.exception.FileNotFoundException;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.FileMetadataService;
import com.samsepiol.file.nexus.metadata.config.FileContentsConsumerConfig;
import com.samsepiol.file.nexus.metadata.message.handler.FilePulseStatusService;
import com.samsepiol.file.nexus.metadata.message.handler.models.filepulse.FilePulseStatusMessage;
import com.samsepiol.file.nexus.metadata.message.handler.models.request.FilePulseServiceRequest;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataSaveRequest;
import com.samsepiol.file.nexus.metadata.parser.FileMetaDataParsingService;
import com.samsepiol.file.nexus.metadata.parser.exception.FileMetaDataParsingException;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataFromFilePulseStatusParsingRequest;
import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import com.samsepiol.file.nexus.metadata.workflow.MetadataStatusWorkflow;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.MetadataStatusWorkflowRequest;
import com.samsepiol.file.nexus.temporal.TemporalService;
import com.samsepiol.library.core.exception.SerializationException;
import com.samsepiol.library.core.util.SerializationUtil;
import com.samsepiol.library.temporal.constants.Queues;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.samsepiol.file.nexus.temporal.utils.IdGeneratorUtil.generateMetadataStatusWorkflowId;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilePulseStatusServiceImpl implements FilePulseStatusService {
    private final TemporalService temporalService;
    private final FileMetaDataParsingService fileMetaDataParsingService;
    private final FileMetadataService fileMetadataService;
    
    private final FileContentsConsumerConfig fileContentsConsumerConfig;

    @Override
    public void updateMetadataStatus(FilePulseServiceRequest request) {
        FilePulseStatusMessage statusMessage = null;
        try {
            statusMessage = SerializationUtil.convertToEntity(request.getMessage(), FilePulseStatusMessage.class);
        } catch (SerializationException e) {
            // TODO
            throw new RuntimeException(e);
        }
        var metaDataParsingRequest = prepareFileMetaDataParsingRequest(statusMessage);

        try {
            var parsedFileMetaData = fileMetaDataParsingService.parse(metaDataParsingRequest);

            if (statusMessage.isCompleted()) {
                startWorkflow(parsedFileMetaData);
            }

        } catch (UnsupportedFileException e) {
            log.info("Unsupported file, hence ignoring...");
            // metricHelper.recordErrorMetric(e.getErrorCode(), e.getMessage());
        } catch (FileMetaDataParsingException e) {
            log.error("Failed to parse file meta data for starting file status update workflow, hence ignoring...", e);
            // metricHelper.recordErrorMetric(e.getErrorCode(), e.getMessage());
        }
    }

    private static FileMetaDataFromFilePulseStatusParsingRequest prepareFileMetaDataParsingRequest(
            FilePulseStatusMessage filePulseStatusMessage) {
        return FileMetaDataFromFilePulseStatusParsingRequest.builder()
                .statusMessage(filePulseStatusMessage)
                .build();
    }

    private void startWorkflow(ParsedFileMetaData parsedFileMetaData) {
        if (canStartWorkflow(parsedFileMetaData)) {

            String workflowId = generateMetadataStatusWorkflowId(parsedFileMetaData.getFileId());
            MetadataStatusWorkflow workflow = metaDataStatusWorkflowStub(workflowId);

            MetadataStatusWorkflowRequest request = MetadataStatusWorkflowRequest.builder()
                    .parsedFileMetaData(parsedFileMetaData)
                    .topicName(fileContentsConsumerConfig.getTopicName())
                    .groupId(fileContentsConsumerConfig.getGroupId())
                    .build();

            startWorkflow(workflow, request);
        }
    }

    private void startWorkflow(MetadataStatusWorkflow workflow,
                               MetadataStatusWorkflowRequest request) {
        try {
            WorkflowClient.start(workflow::updateMetadataStatus, request);
        } catch (WorkflowExecutionAlreadyStarted exception) {
            log.info("Workflow execution already started for fileId: {} and fileName: {}, hence ignoring...",
                    request.getParsedFileMetaData().getFileId(), request.getParsedFileMetaData().getName());
        }
    }

    private boolean canStartWorkflow(ParsedFileMetaData parsedFileMetaData) {
        try {
            var fileMetadata = fileMetadataService.fetchMetadata(parsedFileMetaData.getFileId());

            if (!fileMetadata.getFileName().equals(parsedFileMetaData.getName())) {
                log.warn("Duplicate Metadata status update Workflow attempted for fileId: {} with fileName: {}",
                        parsedFileMetaData.getFileId(), parsedFileMetaData.getName());
                // metricHelper.recordErrorMetric(Error.FILE_METADATA_STATUS_UPDATE_ATTEMPTED_FOR_DUPLICATE_FILE_ID.getCode(),
//                        Error.FILE_METADATA_STATUS_UPDATE_ATTEMPTED_FOR_DUPLICATE_FILE_ID.getMessage());
                return Boolean.FALSE;
            }

        } catch (FileNotFoundException exception) {
            log.info("File not found while starting workflow, attempting creation with fileId: {} and fileName: {}",
                    parsedFileMetaData.getFileId(), parsedFileMetaData.getName());
            fileMetadataService.save(FileMetadataSaveRequest.forPending(parsedFileMetaData));
        }
        return Boolean.TRUE;
    }

    private MetadataStatusWorkflow metaDataStatusWorkflowStub(String workflowId) {
        var workflowOptions = WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(Queues.WORKFLOWS)
                .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
                .build();

        return temporalService.newWorkflow(workflowOptions, MetadataStatusWorkflow.class);
    }

}
