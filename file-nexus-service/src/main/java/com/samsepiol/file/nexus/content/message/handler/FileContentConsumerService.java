package com.samsepiol.file.nexus.content.message.handler;

import com.samsepiol.file.nexus.content.message.handler.models.request.FileContentHandlerServiceRequest;

/**
 * Service handles File content messages consumed from source
 */
public interface FileContentConsumerService {

    // TODO impl bulk content consumption - both message consumer and db bulk save for metadata and contents
    void handle(FileContentHandlerServiceRequest request);

}
