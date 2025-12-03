package com.samsepiol.file.nexus.content.data.parser.config;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.data.parser.impl.FieldMappings;
import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class TudfFileParserConfig extends FileSchemaConfig.SchemaConfig.FileParserConfig {
    Integer recordIdentifierIndex;
    Map<Character, FieldMappings> identifiers;

    @Builder
    public TudfFileParserConfig(Integer recordIdentifierIndex,
                                Map<Character, FieldMappings> identifiers) {
        super(FileParserType.TUDF);
        this.recordIdentifierIndex = recordIdentifierIndex;
        this.identifiers = identifiers;
    }
}
