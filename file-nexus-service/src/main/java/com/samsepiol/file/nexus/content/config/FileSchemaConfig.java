package com.samsepiol.file.nexus.content.config;

import com.samsepiol.file.nexus.content.data.models.enums.Index;
import com.samsepiol.file.nexus.content.data.parser.config.TudfFileParserConfig;
import com.samsepiol.file.nexus.content.data.parser.impl.FieldMappings;
import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import com.samsepiol.file.nexus.content.exception.TudfFileConfigLoadingException;
import com.samsepiol.file.nexus.content.exception.UnknownFileParserConfigException;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.library.core.exception.SerializationException;
import com.samsepiol.library.core.util.SerializationUtil;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "file-config")
public class FileSchemaConfig {
    private List<FileToSchemaConfig> fileToSchemaConfigs;
    private Map<String, SchemaConfig> fileConfigMap;

    @PostConstruct
    protected void init() {
        fileConfigMap = Optional.ofNullable(fileToSchemaConfigs).orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(FileToSchemaConfig::getFileType, FileToSchemaConfig::getSchemaConfig));

        fileConfigMap.values().forEach(SchemaConfig::preparePostConstructFields);

        fileConfigMap.forEach((fileType, schemaConfig) -> log.info(
                "\n\nConfigured Files:\n\nFileType: {}\nParser Type: {}\nMandatory Columns: {}\nColumn to Index Mappings: {}\n\n",
                fileType, schemaConfig.getParserType(), schemaConfig.getMandatoryColumns(), schemaConfig.getColumnToIndexMap()));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileToSchemaConfig {
        private String fileType;
        private SchemaConfig schemaConfig;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchemaConfig {
        private FileParserType parserType;
        private TudfFileParserLoadConfig tudfFileParserLoadConfig;
        private Boolean rowNumberMandatory;
        private List<ColumnNameToColumnConfig> columnConfigs;

        // Post construct fields
        private List<String> mandatoryColumns;
        private Map<String, Index> columnToIndexMap;
        private Map<Index, String> indexToColumnMap;
        TudfFileParserConfig tudfFileParserConfig;


        private void preparePostConstructFields() {
            prepareMandatoryColumns();
            prepareColumnToIndexMap();
            prepareIndexToColumnMap();
            prepareTudfFileParserConfig();
        }

        private Map<Character, FieldMappings> prepareIdentifierToFieldConfigsMap() {
            return tudfFileParserLoadConfig.getIdentifiersToFileName().stream().collect(Collectors.toMap(
                    TudfFileParserLoadConfig.RecordTypeToFileMapping::getIdentifier,
                    recordTypeToFileMapping -> {
                        try {
                            ClassPathResource resource = new ClassPathResource(recordTypeToFileMapping.getFilePath());
                            byte[] data = FileCopyUtils.copyToByteArray(resource.getInputStream());
                            String json = new String(data, StandardCharsets.UTF_8);
                            log.info("[TudfFileParserConfig] Identifier: {}, Config: {}", recordTypeToFileMapping.getIdentifier(), json);
                            return SerializationUtil.convertToEntity(json, FieldMappings.class);
                        } catch (SerializationException | IOException e) {
                            log.info("Error in loading tudf config {}", e);
                            throw TudfFileConfigLoadingException.create();
                        }
                    }));
        }

        private void prepareTudfFileParserConfig() {
            if (Objects.isNull(tudfFileParserLoadConfig) || !parserType.equals(FileParserType.TUDF)) {
                return;
            }

            tudfFileParserConfig = TudfFileParserConfig.builder()
                    .recordIdentifierIndex(tudfFileParserLoadConfig.getRecordIdentifierIndex())
                    .identifiers(prepareIdentifierToFieldConfigsMap())
                    .build();

            log.info("TUDF File Parser Config Loaded");
        }


        private void prepareMandatoryColumns() {
            this.mandatoryColumns = columnConfigs.stream()
                    .filter(columnNameToColumnConfig -> columnNameToColumnConfig.getColumnConfig().getMandatory())
                    .map(ColumnNameToColumnConfig::getColumnName)
                    .toList();
        }

        private void prepareColumnToIndexMap() {
            this.columnToIndexMap = columnConfigs.stream()
                    .filter(columnNameToColumnConfig ->
                            Objects.nonNull(columnNameToColumnConfig.getColumnConfig().getIndex()))
                    .collect(Collectors.toMap(ColumnNameToColumnConfig::getColumnName,
                            columnNameToColumnConfig -> columnNameToColumnConfig.getColumnConfig().getIndex()));
        }

        private void prepareIndexToColumnMap() {
            this.indexToColumnMap = columnConfigs.stream()
                    .filter(entry -> Objects.nonNull(entry.getColumnConfig().getIndex()))
                    .collect(Collectors.toMap(entry -> entry.getColumnConfig().getIndex(), ColumnNameToColumnConfig::getColumnName));
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ColumnNameToColumnConfig {
            private String columnName;
            private ColumnConfig columnConfig;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ColumnConfig {
            @NonNull
            private Boolean mandatory;

            private Index index;
        }

        @Getter
        @NoArgsConstructor(force = true)
        @AllArgsConstructor
        public abstract static class FileParserConfig {
            private final FileParserType parserType;
        }
    }

    public @NonNull List<String> getMandatoryColumns(String fileType) throws UnsupportedFileException {
        return getOptionalSchemaConfig(fileType).getMandatoryColumns();
    }

    public @NonNull Set<String> getQuerySupportedColumns(String fileType) throws UnsupportedFileException {
        return getOptionalSchemaConfig(fileType).getColumnToIndexMap().keySet();
    }

    public @NonNull Set<String> getSupportedFiles() {
        return fileConfigMap.keySet();
    }

    public @NonNull String getColumnToIndex(@NonNull String fileType, @NonNull Index index) throws UnsupportedFileException {
        return getOptionalSchemaConfig(fileType).getIndexToColumnMap().get(index);
    }

    public @NonNull Index getIndexToColumn(@NonNull String fileType, @NonNull String column) throws UnsupportedFileException {
        return getOptionalSchemaConfig(fileType).getColumnToIndexMap().get(column);
    }

    public @NonNull FileParserType getParserType(@NonNull String fileType) throws UnsupportedFileException {
        return getOptionalSchemaConfig(fileType).getParserType();
    }

    public @NonNull SchemaConfig.FileParserConfig getParserConfig(@NonNull String fileType) throws UnsupportedFileException {
        var schemaConfig = getOptionalSchemaConfig(fileType);
        return switch (schemaConfig.getParserType()) {
            case TUDF -> schemaConfig.getTudfFileParserConfig();
            default -> throw UnknownFileParserConfigException.create();
        };
    }

    public SchemaConfig getOptionalSchemaConfig(@NonNull String fileType) throws UnsupportedFileException {
        return Optional.ofNullable(fileConfigMap.get(fileType)).orElseThrow(UnsupportedFileException::create);
    }

    public boolean isRowNumberMandatory(@NonNull String fileType) throws UnsupportedFileException {
        return BooleanUtils.toBoolean(getOptionalSchemaConfig(fileType).getRowNumberMandatory());
    }
}
