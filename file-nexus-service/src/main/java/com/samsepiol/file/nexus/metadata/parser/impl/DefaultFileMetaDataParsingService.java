package com.samsepiol.file.nexus.metadata.parser.impl;

import com.samsepiol.file.nexus.content.config.FileHandlerConfig;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.parser.FileMetaDataParsingService;
import com.samsepiol.file.nexus.metadata.parser.exception.FileMetaDataParsingException;
import com.samsepiol.file.nexus.metadata.parser.models.enums.FileDateParsingSource;
import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import com.samsepiol.file.nexus.metadata.parser.models.request.FileMetaDataParsingServiceRequest;
import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import com.samsepiol.file.nexus.metadata.parser.type.FileCreatedAtTimeParser;
import com.samsepiol.file.nexus.metadata.parser.type.FileDateParser;
import com.samsepiol.file.nexus.metadata.parser.type.FileNameParser;
import com.samsepiol.file.nexus.metadata.parser.type.FileNameSuffixParser;
import com.samsepiol.file.nexus.metadata.parser.type.models.FileDateParserRequest;
import com.samsepiol.file.nexus.metadata.parser.type.models.FileSuffixParserRequest;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import com.samsepiol.file.nexus.utils.FileUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultFileMetaDataParsingService implements FileMetaDataParsingService {
    private final Map<MetaDataSource, FileNameParser> fileNameParserMap;
    private final Map<MetaDataSource, FileCreatedAtTimeParser> fileCreatedAtDateParserMap;
    private final Map<FileDateParsingSource, FileDateParser> fileDateParserMap;
    private final FileNameSuffixParser fileNameSuffixParser;
    private final FileHandlerConfig fileHandlerConfig;

    @Override
    public @NonNull ParsedFileMetaData parse(@NonNull FileMetaDataParsingServiceRequest request)
            throws UnsupportedFileException, FileMetaDataParsingException {
        String fileName = FileUtils.getFileNameWithoutExtension(fileNameParserMap.get(request.getSource()).parse(request));
        String fileType = fileHandlerConfig.getFileType(fileName).orElseThrow(UnsupportedFileException::create);
        var fileDateRequest = prepareFileDateParserRequest(fileName, fileType);
        LocalDate fileDate = fileDateParserMap.get(fileHandlerConfig.getDateParsingSource(fileType)).parse(fileDateRequest);
        Long createdAt = fileCreatedAtDateParserMap.get(request.getSource()).parse(request);
        var fileNameSuffixParserRequest = prepareFileNameSuffixParserRequest(fileName, fileType);
        String fileNameSuffix = fileNameSuffixParser.parse(fileNameSuffixParserRequest);

        return ParsedFileMetaData.builder()
                .fileId(FileUtils.getFileId(fileType, fileDate, fileNameSuffix))
                .name(fileName)
                .fileType(fileType)
                .fileDate(DateTimeUtils.toYYYYMMDD(fileDate))
                .createdAt(createdAt)
                .build();
    }

    private static FileSuffixParserRequest prepareFileNameSuffixParserRequest(String fileName, String fileType) {
        return FileSuffixParserRequest.builder()
                .fileName(fileName)
                .fileType(fileType)
                .build();
    }

    private static FileDateParserRequest prepareFileDateParserRequest(String fileName, String fileType) {
        return FileDateParserRequest.builder()
                .fileName(fileName)
                .fileType(fileType)
                .build();
    }

}
