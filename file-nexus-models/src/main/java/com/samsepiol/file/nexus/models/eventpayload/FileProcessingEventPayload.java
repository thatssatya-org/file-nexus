package com.samsepiol.file.nexus.models.eventpayload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileProcessingEventPayload {
    private String accountNumber;
    private String fileType;
    private String fileProcessingDate;
    private String recordType;
    private Map<String, String> metadata;
}
