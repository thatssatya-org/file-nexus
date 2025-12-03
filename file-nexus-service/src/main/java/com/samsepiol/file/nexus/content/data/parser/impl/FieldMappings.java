package com.samsepiol.file.nexus.content.data.parser.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FieldMappings {

    List<FieldMapping> fieldMapping;
}
