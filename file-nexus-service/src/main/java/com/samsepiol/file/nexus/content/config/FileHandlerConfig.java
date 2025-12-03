package com.samsepiol.file.nexus.content.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.metadata.parser.models.enums.FileDateFormat;
import com.samsepiol.file.nexus.metadata.parser.models.enums.FileDateParsingSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "file-handler-config")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FileHandlerConfig {
    private List<FilePrefixToType> filePrefixToTypes;
    private List<FileTypeToMetadataParsingConfig> metadataParsingConfigs;

    // Post construct fields
    private Map<String, String> prefixToTypeMap;
    private Map<String, MetadataParsingConfig> metadataParsingConfigMap;

    @PostConstruct
    protected void init() {
        prefixToTypeMap = Optional.ofNullable(filePrefixToTypes).orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(FilePrefixToType::getPrefix, FilePrefixToType::getFileType));

        metadataParsingConfigMap = Optional.ofNullable(metadataParsingConfigs).orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(
                        FileTypeToMetadataParsingConfig::getFileType, FileTypeToMetadataParsingConfig::getMetadataParsingConfig));

        log.info("\n\nFile metadata configs:\n\nPrefix to Type Mappings: {}\nMetadata Parsing Config: {}\n\n",
                prefixToTypeMap, metadataParsingConfigMap);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FilePrefixToType {
        private String prefix;
        private String fileType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FileTypeToMetadataParsingConfig {
        private String fileType;
        private MetadataParsingConfig metadataParsingConfig;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class MetadataParsingConfig {
        private FileDateParsingSource dateParsingSource;
        private FileDateFormat dateFormat;
        private Boolean suffixMandatory;
        private List<String> fileNameSuffixes;
    }

    public @NonNull Optional<String> getFileType(@NonNull String fileName) {
        return prefixToTypeMap.entrySet().stream()
                .filter(entry -> fileName.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public @NonNull FileDateParsingSource getDateParsingSource(@NonNull String fileType) throws UnsupportedFileException {
        return getMetadataParsingConfig(fileType).getDateParsingSource();
    }

    public @NonNull List<String> getSupportedFileNameSuffixes(@NonNull String fileType) throws UnsupportedFileException {
        return Optional.ofNullable(getMetadataParsingConfig(fileType).getFileNameSuffixes()).orElse(Collections.emptyList());
    }

    public @NonNull FileDateFormat getFileDateFormat(@NonNull String fileType) throws UnsupportedFileException {
        return getMetadataParsingConfig(fileType).getDateFormat();
    }

    public boolean isSuffixMandatory(@NonNull String fileType) throws UnsupportedFileException {
        var config = getMetadataParsingConfig(fileType);
        return CollectionUtils.isNotEmpty(config.getFileNameSuffixes()) && Objects.requireNonNullElse(config.getSuffixMandatory(), Boolean.TRUE);
    }

    private MetadataParsingConfig getMetadataParsingConfig(String fileType) throws UnsupportedFileException {
        return Optional.ofNullable(metadataParsingConfigMap.get(fileType)).orElseThrow(UnsupportedFileException::create);
    }

}
