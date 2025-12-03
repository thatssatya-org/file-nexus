package com.samsepiol.file.nexus.storage.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.samsepiol.file.nexus.storage.config.destination.AbstractDestinationConfig;
import com.samsepiol.file.nexus.storage.config.destination.KafkaConfig;
import com.samsepiol.file.nexus.storage.config.destination.SftpConfig;
import com.samsepiol.file.nexus.storage.config.destination.SmtpConfig;
import com.samsepiol.file.nexus.storage.config.source.AbstractSourceConfig;
import com.samsepiol.file.nexus.storage.config.source.S3GcsSourceConfig;
import com.samsepiol.file.nexus.storage.config.source.SftpSourceConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConfigurationFactory {

    public static final String TYPE_FIELD = "type";
    private final ObjectMapper objectMapper;

    public ConfigurationFactory() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.coercionConfigFor(LogicalType.Enum)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public AbstractSourceConfig createSourceConfig(Map<String, Object> sourceMap) {
        String type = (String) sourceMap.get(TYPE_FIELD);
        if (type == null) {
            throw new IllegalArgumentException("Source type is required");
        }

        try {
            return switch (type.toUpperCase()) {
                case "S3", "GCS" -> objectMapper.convertValue(sourceMap, S3GcsSourceConfig.class);
                case "SFTP" -> objectMapper.convertValue(sourceMap, SftpSourceConfig.class);
                default -> throw new IllegalArgumentException("Unknown source type: " + type);
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to create source config for type: " + type, e);
        }
    }

    public AbstractDestinationConfig createDestinationConfig(Map<String, Object> destMap) {
        String type = (String) destMap.get(TYPE_FIELD);
        if (type == null) {
            throw new IllegalArgumentException("Destination type is required");
        }

        try {
            return switch (type.toUpperCase()) {
                case "SMTP" -> objectMapper.convertValue(destMap, SmtpConfig.class);
                case "SFTP" -> objectMapper.convertValue(destMap, SftpConfig.class);
                case "KAFKA" -> objectMapper.convertValue(destMap, KafkaConfig.class);
                default -> throw new IllegalArgumentException("Unknown destination type: " + type);
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to create destination config for type: " + type, e);
        }
    }

    public List<AbstractDestinationConfig> createDestinationConfigs(Map<String, Object> destMaps) {
        return destMaps.values().stream()
                .map(val -> (Map<String, Object>) val)
                .map(this::createDestinationConfig)
                .collect(Collectors.toList());
    }
}
