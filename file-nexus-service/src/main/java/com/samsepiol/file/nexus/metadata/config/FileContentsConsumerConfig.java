package com.samsepiol.file.nexus.metadata.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file-contents-consumer-config")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FileContentsConsumerConfig {
    private String groupId;
    private String topicName;
}
