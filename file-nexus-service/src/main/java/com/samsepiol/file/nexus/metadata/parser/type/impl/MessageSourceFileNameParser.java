package com.samsepiol.file.nexus.metadata.parser.type.impl;

import com.samsepiol.file.nexus.metadata.parser.exception.MissingFileNameInFilePulseStatusException;
import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataFromFilePulseStatusParsingRequest;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingServiceRequest;
import com.samsepiol.file.nexus.metadata.parser.type.FileNameParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageSourceFileNameParser implements FileNameParser {

    @Override
    public @NonNull String parse(@NonNull FileMetaDataParsingServiceRequest request) throws MissingFileNameInFilePulseStatusException {
        var parsingRequest = (FileMetaDataFromFilePulseStatusParsingRequest) request;
        return Optional.ofNullable(parsingRequest.getStatusMessage().getFileName())
                .orElseThrow(MissingFileNameInFilePulseStatusException::create);
    }

    @Override
    public @NonNull MetaDataSource getSource() {
        return MetaDataSource.FILE_PULSE_STATUS_MESSAGE;
    }
}
