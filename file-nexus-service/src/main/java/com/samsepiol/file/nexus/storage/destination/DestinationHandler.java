package com.samsepiol.file.nexus.storage.destination;

import com.samsepiol.file.nexus.storage.destination.models.SendFileRequestDto;

import java.io.IOException;

/**
 * Interface for destination handlers that send files to different destinations.
 * Implementations should handle sending files to specific destinations like SMTP, SFTP, etc.
 */
public interface DestinationHandler {

    /**
     * Get the type of destination this handler supports.
     *
     * @return The destination type
     */
    DestinationType getType();

    /**
     * Check if this handler can handle the given destination type.
     *
     * @param type The destination types to check
     * @return true if this handler can handle the given destination type, false otherwise
     */
    default boolean canHandle(DestinationType type) {
        return getType() == type;
    }

    boolean sendFile(SendFileRequestDto sendFileRequestDto) throws IOException;
}
