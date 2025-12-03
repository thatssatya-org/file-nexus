package com.samsepiol.file.nexus.metadata.parser.models.request;

import com.samsepiol.file.nexus.metadata.message.handler.models.filepulse.FilePulseStatusMessage;
import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class FileMetaDataFromFilePulseStatusParsingRequest extends FileMetaDataParsingServiceRequest {
    @NonNull
    FilePulseStatusMessage statusMessage;

    @Builder
    public FileMetaDataFromFilePulseStatusParsingRequest(@NonNull FilePulseStatusMessage statusMessage) {
        super(MetaDataSource.FILE_PULSE_STATUS_MESSAGE);
        this.statusMessage = statusMessage;
    }
}
