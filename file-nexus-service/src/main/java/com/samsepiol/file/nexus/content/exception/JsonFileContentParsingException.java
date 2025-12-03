package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class JsonFileContentParsingException extends FileContentParsingException {

    @Builder(access = AccessLevel.PRIVATE)
    private JsonFileContentParsingException() {
        super(Error.JSON_FILE_CONTENT_PARSING_FAILURE);
    }

    public static JsonFileContentParsingException create() {
        return JsonFileContentParsingException.builder().build();
    }
}
