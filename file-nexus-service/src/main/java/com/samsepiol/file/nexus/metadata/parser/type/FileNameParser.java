package com.samsepiol.file.nexus.metadata.parser.type;

import com.samsepiol.file.nexus.metadata.parser.exception.MissingFileNameInFilePulseStatusException;
import com.samsepiol.file.nexus.metadata.parser.exception.MissingFileNameInMessageHeadersException;
import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingServiceRequest;
import lombok.NonNull;

public interface FileNameParser {

    @NonNull
    String parse(@NonNull FileMetaDataParsingServiceRequest request)
            throws MissingFileNameInFilePulseStatusException, MissingFileNameInMessageHeadersException;

    @NonNull
    MetaDataSource getSource();
}
