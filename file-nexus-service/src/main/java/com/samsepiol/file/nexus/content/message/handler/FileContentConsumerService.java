package com.samsepiol.file.nexus.content.message.handler;

import com.samsepiol.file.nexus.content.message.handler.models.request.FileContentHandlerServiceRequest;

/**
 * Service handles File content messages consumed from Message Queue
 */
public interface FileContentConsumerService {

    void handleFileContent(FileContentHandlerServiceRequest request);

}
