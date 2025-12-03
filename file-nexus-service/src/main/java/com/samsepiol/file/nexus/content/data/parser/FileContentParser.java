package com.samsepiol.file.nexus.content.data.parser;

import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import com.samsepiol.file.nexus.content.data.parser.models.request.FileContentParsingRequest;
import com.samsepiol.file.nexus.content.exception.FileContentParsingException;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import lombok.NonNull;

import java.util.Map;

public interface FileContentParser {

    @NonNull
    Map<String, Object> parse(@NonNull FileContentParsingRequest request)
            throws FileContentParsingException, UnsupportedFileException;

    @NonNull
    FileParserType getType();
}
