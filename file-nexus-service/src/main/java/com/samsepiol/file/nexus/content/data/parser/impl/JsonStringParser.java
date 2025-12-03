package com.samsepiol.file.nexus.content.data.parser.impl;

import com.samsepiol.file.nexus.content.data.parser.FileContentParser;
import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import com.samsepiol.file.nexus.content.data.parser.models.request.FileContentParsingRequest;
import com.samsepiol.file.nexus.content.exception.JsonFileContentParsingException;
import com.samsepiol.library.core.util.SerializationUtil;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JsonStringParser implements FileContentParser {

    @Override
    public @NonNull Map<String, Object> parse(@NonNull FileContentParsingRequest request) throws JsonFileContentParsingException {
        try {
            return SerializationUtil.convertToMap(request.getContent());
        } catch (Exception e) {
            throw JsonFileContentParsingException.create();
        }
    }

    @Override
    public @NonNull FileParserType getType() {
        return FileParserType.JSON;
    }
}
