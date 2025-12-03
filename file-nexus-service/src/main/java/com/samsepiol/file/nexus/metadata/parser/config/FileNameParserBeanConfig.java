package com.samsepiol.file.nexus.metadata.parser.config;

import com.samsepiol.file.nexus.metadata.parser.models.enums.FileDateParsingSource;
import com.samsepiol.file.nexus.metadata.parser.models.enums.MetaDataSource;
import com.samsepiol.file.nexus.metadata.parser.type.FileCreatedAtTimeParser;
import com.samsepiol.file.nexus.metadata.parser.type.FileDateParser;
import com.samsepiol.file.nexus.metadata.parser.type.FileNameParser;
import com.samsepiol.file.nexus.utils.ClassUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class FileNameParserBeanConfig {

    @Bean
    public Map<MetaDataSource, FileNameParser> fileNameParserMap(List<FileNameParser> fileNameParsers) {
        return fileNameParsers.stream().collect(Collectors.toMap(FileNameParser::getSource, ClassUtils.unary()));
    }

    @Bean
    public Map<FileDateParsingSource, FileDateParser> fileDateParserMap(List<FileDateParser> fileDateParsers) {
        return fileDateParsers.stream().collect(Collectors.toMap(FileDateParser::getSource, ClassUtils.unary()));
    }

    @Bean
    public Map<MetaDataSource, FileCreatedAtTimeParser> fileCreatedAtTimeParserMap(List<FileCreatedAtTimeParser> fileCreatedAtTimeParsers) {
        return fileCreatedAtTimeParsers.stream().collect(Collectors.toMap(FileCreatedAtTimeParser::getSource, ClassUtils.unary()));
    }
}
