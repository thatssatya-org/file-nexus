package com.samsepiol.file.nexus.content.validation;

import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.content.models.request.FileContentFetchServiceRequest;
import com.samsepiol.file.nexus.content.validation.response.ValidationResponse;
import lombok.NonNull;

import java.util.Map;

public interface FileContentRequestValidation {
    enum Type {
        FILE_TYPE, SUPPORTED_QUERY_COLUMN
    }

    Map<Type, Integer> ORDER_MAP = Map.of(
            Type.FILE_TYPE, 1,
            Type.SUPPORTED_QUERY_COLUMN, 2
    );

    @NonNull
    ValidationResponse validate(@NonNull FileContentFetchServiceRequest request) throws UnsupportedFileException;

    @NonNull Type getType();

    default @NonNull Integer getOrder() {
        return ORDER_MAP.get(getType());
    }
}
