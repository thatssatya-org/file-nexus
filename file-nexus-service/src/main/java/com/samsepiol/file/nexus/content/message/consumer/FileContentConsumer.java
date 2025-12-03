package com.samsepiol.file.nexus.content.message.consumer;

import com.samsepiol.file.nexus.constants.BeanNameConstants;
import com.samsepiol.file.nexus.content.message.handler.FileContentConsumerService;
import com.samsepiol.file.nexus.content.message.handler.models.request.FileContentHandlerServiceRequest;
import com.samsepiol.kafka.processor.IMessageConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component(BeanNameConstants.MessageConsumers.FILE_CONTENTS_CONSUMER)
public class FileContentConsumer implements IMessageConsumer.MessageHandler {
    private final FileContentConsumerService fileContentConsumerService;

    @Override
    public void handleMessage(String message, String metadata, Map<String, String> headers) {
        log.info("File content message received: {}", message);
        fileContentConsumerService.handleFileContent(createFileHandlerRequest(headers, message));
    }

    private FileContentHandlerServiceRequest createFileHandlerRequest(Map<String, String> headers, String message) {
        return FileContentHandlerServiceRequest.builder()
                .headers(headers)
                .message(message)
                .build();
    }
}
