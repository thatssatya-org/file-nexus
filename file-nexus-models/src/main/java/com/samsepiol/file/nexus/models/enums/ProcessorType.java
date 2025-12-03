package com.samsepiol.file.nexus.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProcessorType {
    CSV_TO_JSON("CSV_TO_JSON");

    private final String name;
}
