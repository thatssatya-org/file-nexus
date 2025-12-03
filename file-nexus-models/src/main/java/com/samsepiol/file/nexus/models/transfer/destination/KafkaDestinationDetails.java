package com.samsepiol.file.nexus.models.transfer.destination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaDestinationDetails {
    private String topicName;
}
