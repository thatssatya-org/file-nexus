package com.samsepiol.file.nexus.models.content.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileContentResponse {
    String rowReferenceId;
    String fileId;
    String rowNumber;
    Map<String, Object> content;
}
