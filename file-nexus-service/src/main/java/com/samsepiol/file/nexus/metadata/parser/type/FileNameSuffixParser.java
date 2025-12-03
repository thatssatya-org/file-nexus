package com.samsepiol.file.nexus.metadata.parser.type;

import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.parser.exception.MandatoryFileNameSuffixMissingException;
import com.samsepiol.file.nexus.metadata.parser.type.models.FileSuffixParserRequest;
import lombok.NonNull;

public interface FileNameSuffixParser {

    String parse(@NonNull FileSuffixParserRequest request)
            throws UnsupportedFileException, MandatoryFileNameSuffixMissingException;
}
