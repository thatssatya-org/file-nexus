package com.samsepiol.file.nexus.metadata.parser.type.impl;

import com.samsepiol.file.nexus.metadata.parser.exception.MissingFileNameInMessageHeadersException;
import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingFromHeadersRequest;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingServiceRequest;
import com.samsepiol.file.nexus.metadata.parser.type.FileNameParser;
import com.samsepiol.file.nexus.utils.FilePulseConnectorUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeaderSourceFileNameParser implements FileNameParser {

    @Override
    public @NonNull String parse(@NonNull FileMetaDataParsingServiceRequest request) throws MissingFileNameInMessageHeadersException {
        var headersRequest = (FileMetaDataParsingFromHeadersRequest) request;
        return FilePulseConnectorUtils.getFileNameFromHeaders(headersRequest.getHeaders())
                .orElseThrow(MissingFileNameInMessageHeadersException::create);
    }

    @Override
    public @NonNull MetaDataSource getSource() {
        return MetaDataSource.FILE_CONTENT_MESSAGE_HEADERS;
    }
}
