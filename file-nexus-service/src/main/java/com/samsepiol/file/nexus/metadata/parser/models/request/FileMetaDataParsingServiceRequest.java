package com.samsepiol.file.nexus.metadata.parser.models.request;

import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMetaDataParsingServiceRequest {
    @NonNull
    private final MetaDataSource source;
}
