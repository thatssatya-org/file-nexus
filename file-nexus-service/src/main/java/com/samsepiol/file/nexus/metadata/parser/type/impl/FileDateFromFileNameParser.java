package com.samsepiol.file.nexus.metadata.parser.type.impl;

import com.samsepiol.file.nexus.content.config.FileHandlerConfig;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.parser.exception.FileDateFromNameParsingException;
import com.samsepiol.file.nexus.metadata.parser.models.enums.FileDateParsingSource;
import com.samsepiol.file.nexus.metadata.parser.type.FileDateParser;
import com.samsepiol.file.nexus.metadata.parser.type.models.FileDateParserRequest;
import com.samsepiol.file.nexus.utils.FilePulseConnectorUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDateFromFileNameParser implements FileDateParser {
    private final FileHandlerConfig fileHandlerConfig;

    @Override
    public @NonNull LocalDate parse(@NonNull FileDateParserRequest request)
            throws FileDateFromNameParsingException, UnsupportedFileException {
        try {
            var optionalDate = getDateFromFileName(request);
            return optionalDate.orElseThrow(FileDateFromNameParsingException::create);
        } catch (DateTimeParseException exception) {
            log.error("Error parsing date from file name: {}", request.getFileName(), exception);
            throw FileDateFromNameParsingException.create();
        }
    }

    private Optional<LocalDate> getDateFromFileName(FileDateParserRequest request) throws UnsupportedFileException {
        return switch (fileHandlerConfig.getFileDateFormat(request.getFileType())) {
            case YYYYMMDD -> FilePulseConnectorUtils
                    .getDateFromFileNameContainedAsYYYYMMDD(request.getFileName(), "_", 1);
            case DDMMYYYY ->  FilePulseConnectorUtils
                    .getDateFromFileNameContainedAsDDMMYYYY(request.getFileName(), "_", 1);
        };
    }

    @Override
    public @NonNull FileDateParsingSource getSource() {
        return FileDateParsingSource.NAME;
    }
}
