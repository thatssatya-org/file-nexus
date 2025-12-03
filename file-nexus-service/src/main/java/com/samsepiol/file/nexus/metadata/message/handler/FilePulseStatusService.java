package com.samsepiol.file.nexus.metadata.message.handler;

import com.samsepiol.file.nexus.metadata.message.handler.models.request.FilePulseServiceRequest;

/**
 * Handles File pulse status messages
 */
public interface FilePulseStatusService {

    /**
     * Update file metadata status based on file-pulse status message
     * @param request Service request
     */
    void updateMetadataStatus(FilePulseServiceRequest request);
}
