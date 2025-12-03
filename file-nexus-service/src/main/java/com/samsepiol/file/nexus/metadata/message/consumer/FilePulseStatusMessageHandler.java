package com.samsepiol.file.nexus.metadata.message.consumer;

import com.samsepiol.file.nexus.constants.BeanNameConstants;
import com.samsepiol.file.nexus.metadata.message.handler.FilePulseStatusService;
import com.samsepiol.file.nexus.metadata.message.handler.models.request.FilePulseServiceRequest;
import com.samsepiol.kafka.processor.IMessageConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component(BeanNameConstants.MessageConsumers.FILE_PULSE_STATUS_CONSUMER)
public class FilePulseStatusMessageHandler implements IMessageConsumer.MessageHandler {
    private final FilePulseStatusService filePulseService;

    @Override
    public void handleMessage(String message, String metadata, Map<String, String> headers) {
        log.info("File pulse message received: {}", message);
        filePulseService.updateMetadataStatus(createFilePulseRequest(headers, message));
    }

    private FilePulseServiceRequest createFilePulseRequest(Map<String, String> headers, String message) {
        return FilePulseServiceRequest.builder()
                .headers(headers)
                .message(message)
                .build();
    }

}

