package com.samsepiol.file.nexus.metadata.parser.type.impl;

import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataFromFilePulseStatusParsingRequest;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingServiceRequest;
import com.samsepiol.file.nexus.metadata.parser.type.FileCreatedAtTimeParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSourceFileCreatedAtParser implements FileCreatedAtTimeParser {

    @Override
    public Long parse(@NonNull FileMetaDataParsingServiceRequest request) {
        var parsingRequest = (FileMetaDataFromFilePulseStatusParsingRequest) request;
        return parsingRequest.getStatusMessage().getLastModified();
    }

    @Override
    public @NonNull MetaDataSource getSource() {
        return MetaDataSource.FILE_PULSE_STATUS_MESSAGE;
    }
}
