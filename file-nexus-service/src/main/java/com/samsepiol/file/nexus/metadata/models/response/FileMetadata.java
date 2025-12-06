package com.samsepiol.file.nexus.metadata.models.response;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;




@Value
@Builder
public class FileMetadata {

    @NonNull
    String id;

    @NonNull
    String fileType;

    @NonNull
    String date;

    @NonNull
    String fileName;

    @NonNull
    MetadataStatus status;

    public boolean isStatusPending() {
        return MetadataStatus.PENDING == status;
    }

    public boolean isStatusDiscarded() {
        return MetadataStatus.DISCARDED == status;
    }

    public boolean isStatusCompleted() {
        return MetadataStatus.COMPLETED == status;
    }

}
