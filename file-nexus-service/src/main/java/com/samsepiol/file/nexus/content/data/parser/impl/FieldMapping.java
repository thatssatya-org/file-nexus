package com.samsepiol.file.nexus.content.data.parser.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FieldMapping {
    Integer start;
    String fieldName;
    Integer end;
}
