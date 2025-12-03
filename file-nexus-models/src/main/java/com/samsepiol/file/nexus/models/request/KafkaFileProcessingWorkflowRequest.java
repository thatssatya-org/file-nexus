package com.samsepiol.file.nexus.models.request;

import com.samsepiol.file.nexus.models.config.KafkaDestinationConfig;
import com.samsepiol.file.nexus.models.dto.BaseDestinationConfigDto;
import com.samsepiol.file.nexus.models.dto.FileDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaFileProcessingWorkflowRequest {

    private String fileName;
    private KafkaDestinationConfig kafkaDestinationConfig;
    private FileDetails fileDetails;
    private String sourceName;
    private BaseDestinationConfigDto baseDestinationConfig;
}
