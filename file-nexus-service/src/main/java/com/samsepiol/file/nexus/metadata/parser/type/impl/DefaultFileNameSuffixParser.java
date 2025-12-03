package com.samsepiol.file.nexus.metadata.parser.type.impl;

import com.samsepiol.file.nexus.content.config.FileHandlerConfig;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.parser.exception.MandatoryFileNameSuffixMissingException;
import com.samsepiol.file.nexus.metadata.parser.type.FileNameSuffixParser;
import com.samsepiol.file.nexus.metadata.parser.type.models.FileSuffixParserRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultFileNameSuffixParser implements FileNameSuffixParser {
    private final FileHandlerConfig fileConfig;
    private static final String FILE_NAME_SUFFIX_FORMAT = "_%s";

    @Override
    public String parse(@NonNull FileSuffixParserRequest request)
            throws UnsupportedFileException, MandatoryFileNameSuffixMissingException {

        String fileNameSuffix = fileConfig.getSupportedFileNameSuffixes(request.getFileType()).stream()
                .filter(suffix -> request.getFileName().endsWith(String.format(FILE_NAME_SUFFIX_FORMAT, suffix)))
                .findFirst()
                .orElse(null);

        if (fileConfig.isSuffixMandatory(request.getFileType()) && StringUtils.isBlank(fileNameSuffix)) {
            log.warn("File suffix is mandatory but is missing for fileType: {} and fileName: {}, supported suffixes: {}",
                    request.getFileType(), request.getFileName(), fileConfig.getSupportedFileNameSuffixes(request.getFileType()));
            throw MandatoryFileNameSuffixMissingException.create();
        }

        return fileNameSuffix;
    }
}
