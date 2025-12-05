package com.samsepiol.file.nexus.storage.config.destination;

import com.samsepiol.file.nexus.models.enums.ProcessorType;
import com.samsepiol.file.nexus.storage.destination.DestinationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KafkaConfig extends AbstractDestinationConfig {
    private String topic;
    private ProcessorType processorType;
    private String key;

    @Override
    public DestinationType getType() {
        return DestinationType.KAFKA;
    }

}
