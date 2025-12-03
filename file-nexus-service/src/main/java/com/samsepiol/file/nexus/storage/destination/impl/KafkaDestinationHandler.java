package com.samsepiol.file.nexus.storage.destination.impl;

import com.samsepiol.file.nexus.ingestion.workflow.IKafkaFileProcessingWorkflow;
import com.samsepiol.file.nexus.models.config.KafkaDestinationConfig;
import com.samsepiol.file.nexus.models.dto.BaseDestinationConfigDto;
import com.samsepiol.file.nexus.models.dto.FileDetails;
import com.samsepiol.file.nexus.models.request.KafkaFileProcessingWorkflowRequest;
import com.samsepiol.file.nexus.storage.config.destination.KafkaConfig;
import com.samsepiol.file.nexus.storage.destination.DestinationHandler;
import com.samsepiol.file.nexus.storage.destination.DestinationType;
import com.samsepiol.file.nexus.storage.destination.models.SendFileRequestDto;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import io.temporal.client.WorkflowClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.samsepiol.file.nexus.ingestion.workflow.utils.WorkflowOptionsRegistry.getIKafkaFileProcessingWorkflowOptions;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDestinationHandler implements DestinationHandler {

    public static final String WF_PREFIX = "kafka-processing-";
    private final WorkflowClient workflowClient;

    @Override
    public DestinationType getType() {
        return DestinationType.KAFKA;
    }

    @Override
    public boolean canHandle(DestinationType type) {
        return getType() == type;
    }

    @Override
    public boolean sendFile(SendFileRequestDto sendFileRequestDto) {
        if (!(sendFileRequestDto.getConfig() instanceof KafkaConfig kafkaConfig)) {
            log.error("Invalid configuration for Kafka destination. Expected KafkaConfig but got {}", sendFileRequestDto.getConfig().getClass().getName());
            return false;
        }

        log.info("Initiating Kafka processing workflow for file {} to topic {}", sendFileRequestDto.getFileName(), kafkaConfig.getTopic());

        FileInfo fileInfo = sendFileRequestDto.getFileInfo();

        KafkaDestinationConfig kafkaModelDestinationConfig = new KafkaDestinationConfig(
                kafkaConfig.getTopic(),
                kafkaConfig.getProcessorType() != null ? kafkaConfig.getProcessorType().name() : null,
                kafkaConfig.getKey() != null ? kafkaConfig.getKey() : null
        );

        KafkaFileProcessingWorkflowRequest workflowRequest = new KafkaFileProcessingWorkflowRequest(
                sendFileRequestDto.getFileName(),
                kafkaModelDestinationConfig,
                new FileDetails(
                        fileInfo.getFileKey(),
                        fileInfo.getFilePath(),
                        fileInfo.getSize()
                ),
                sendFileRequestDto.getSourceName(),
                BaseDestinationConfigDto.builder()
                        .name(kafkaConfig.getName())
                        .enabled(kafkaConfig.isEnabled())
                        .type(kafkaConfig.getType().name())
                        .build()
        );

        String workflowId = WF_PREFIX + sendFileRequestDto.getFileName();
        IKafkaFileProcessingWorkflow workflow = workflowClient.newWorkflowStub(IKafkaFileProcessingWorkflow.class,
                getIKafkaFileProcessingWorkflowOptions(workflowId));
        WorkflowClient.start(workflow::processFileToKafka, workflowRequest);

        log.info("Asynchronous Temporal workflow started for Kafka processing of file {}", sendFileRequestDto.getFileName());
        return true;
    }
}
