package com.samsepiol.file.nexus.metadata.parser.type.impl;

import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingFromHeadersRequest;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingServiceRequest;
import com.samsepiol.file.nexus.metadata.parser.type.FileCreatedAtTimeParser;
import com.samsepiol.file.nexus.utils.FilePulseConnectorUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeaderSourceFileCreatedAtParser implements FileCreatedAtTimeParser {

    @Override
    public @NonNull Long parse(@NonNull FileMetaDataParsingServiceRequest request) {
        var headersRequest = (FileMetaDataParsingFromHeadersRequest) request;
        return FilePulseConnectorUtils.getCreatedAt(headersRequest.getHeaders());
    }

    @Override
    public @NonNull MetaDataSource getSource() {
        return MetaDataSource.FILE_CONTENT_MESSAGE_HEADERS;
    }
}
