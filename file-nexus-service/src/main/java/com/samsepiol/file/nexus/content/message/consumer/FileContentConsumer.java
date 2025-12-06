package com.samsepiol.file.nexus.content.message.consumer;

import com.samsepiol.file.nexus.common.models.enums.FileNexusMessageHandler;
import com.samsepiol.file.nexus.content.message.handler.FileContentConsumerService;
import com.samsepiol.file.nexus.content.message.handler.models.request.FileContentHandlerServiceRequest;
import com.samsepiol.message.queue.core.MessageHandler;
import com.samsepiol.message.queue.core.models.MessageHandlerType;
import com.samsepiol.message.queue.core.models.request.MessageHandlerRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class FileContentConsumer implements MessageHandler {
    private final FileContentConsumerService fileContentConsumerService;

    @Override
    public void process(MessageHandlerRequest request) {
        log.info("File content message received: {}", request.getValue());
        fileContentConsumerService.handle(createFileHandlerRequest(request));
    }

    @Override
    public @NonNull MessageHandlerType getType() {
        return FileNexusMessageHandler.FILE_CONTENTS;
    }

    private FileContentHandlerServiceRequest createFileHandlerRequest(MessageHandlerRequest request) {
        return FileContentHandlerServiceRequest.builder()
                .metadata(request.getHeaders())
                .message(request.getValue())
                .build();
    }

}
