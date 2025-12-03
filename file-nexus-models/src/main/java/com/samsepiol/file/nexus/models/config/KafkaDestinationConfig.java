package com.samsepiol.file.nexus.models.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaDestinationConfig {

    private String topicName;
    private String processorType;

    /**
     * The key for the Kafka message. This is used to partition messages in Kafka.
     * fallback to null if not specified.
     */
    private String key;
}