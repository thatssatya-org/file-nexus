package com.samsepiol.file.nexus.content.data.parser.config;

import com.samsepiol.file.nexus.content.data.parser.FileContentParser;
import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import com.samsepiol.file.nexus.utils.ClassUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class FileContentParserBeanConfig {

    @Bean
    public Map<FileParserType, FileContentParser> fileContentParserMap(List<FileContentParser> fileContentParsers) {
        return fileContentParsers.stream().collect(Collectors.toMap(FileContentParser::getType, ClassUtils.unary()));
    }
}
