package com.samsepiol.file.nexus.metadata.message.handler.models.filepulse;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class FilePulseStatusMessage {
    @NonNull
    String status;

    @NonNull
    Metadata metadata;

    @Value
    @Jacksonized
    @Builder
    public static class Metadata {
        String name;

        Long lastModified;
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public String getFileName() {
        return metadata.getName();
    }

    public Long getLastModified() {
        return metadata.getLastModified();
    }
}
