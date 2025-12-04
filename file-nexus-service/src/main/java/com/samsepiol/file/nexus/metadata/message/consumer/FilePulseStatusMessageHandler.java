package com.samsepiol.file.nexus.metadata.message.consumer;

import com.samsepiol.file.nexus.common.models.enums.FileNexusMessageHandler;
import com.samsepiol.file.nexus.metadata.message.handler.FilePulseStatusService;
import com.samsepiol.file.nexus.metadata.message.handler.models.request.FilePulseServiceRequest;
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
public class FilePulseStatusMessageHandler implements MessageHandler {
    private final FilePulseStatusService filePulseService;

    @Override
    public void process(MessageHandlerRequest request) {
        log.info("File pulse message received: {}", request.getValue());
        filePulseService.updateMetadataStatus(createFilePulseRequest(request));
    }

    @Override
    public @NonNull MessageHandlerType getType() {
        return FileNexusMessageHandler.FILE_PULSE_STATUS;
    }

    private FilePulseServiceRequest createFilePulseRequest(MessageHandlerRequest request) {
        return FilePulseServiceRequest.builder()
                .headers(request.getHeaders())
                .message(request.getValue())
                .build();
    }
}

