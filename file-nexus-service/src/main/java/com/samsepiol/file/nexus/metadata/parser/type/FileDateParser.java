package com.samsepiol.file.nexus.metadata.parser.type;

import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.parser.exception.FileDateFromNameParsingException;
import com.samsepiol.file.nexus.metadata.parser.models.enums.FileDateParsingSource;
import com.samsepiol.file.nexus.metadata.parser.type.models.FileDateParserRequest;
import lombok.NonNull;

import java.time.LocalDate;

public interface FileDateParser {

    @NonNull
    LocalDate parse(@NonNull FileDateParserRequest request) throws FileDateFromNameParsingException, UnsupportedFileException;

    @NonNull
    FileDateParsingSource getSource();
}
