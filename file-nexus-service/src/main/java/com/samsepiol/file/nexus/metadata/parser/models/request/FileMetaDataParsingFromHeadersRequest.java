package com.samsepiol.file.nexus.metadata.parser.models.request;

import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class FileMetaDataParsingFromHeadersRequest extends FileMetaDataParsingServiceRequest {
    @NonNull
    Map<String, String> headers;

    @Builder
    public FileMetaDataParsingFromHeadersRequest(@NonNull Map<String, String> headers) {
        super(MetaDataSource.FILE_CONTENT_MESSAGE_HEADERS);
        this.headers = headers;
    }
}
