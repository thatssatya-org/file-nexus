package com.samsepiol.file.nexus.metadata.parser;

import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.parser.exception.FileMetaDataParsingException;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingServiceRequest;
import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import lombok.NonNull;

/**
 * Parser file metadata like name, date, etc
 */
public interface FileMetaDataParsingService {

    @NonNull
    ParsedFileMetaData parse(@NonNull FileMetaDataParsingServiceRequest request)
            throws UnsupportedFileException, FileMetaDataParsingException;

}
