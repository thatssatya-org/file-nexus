package com.samsepiol.file.nexus.metadata.parser.type;

import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingServiceRequest;
import lombok.NonNull;

public interface FileCreatedAtTimeParser {

    Long parse(@NonNull FileMetaDataParsingServiceRequest request);

    @NonNull
    MetaDataSource getSource();
}
