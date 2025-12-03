package com.samsepiol.file.nexus.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseDestinationConfigDto {
    private String name;
    private boolean enabled;
    private String type; // Representing DestinationType as String
}
