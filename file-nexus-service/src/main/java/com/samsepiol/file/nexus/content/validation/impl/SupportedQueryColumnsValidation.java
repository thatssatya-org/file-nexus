package com.samsepiol.file.nexus.content.validation.impl;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.content.models.request.FileContentFetchServiceRequest;
import com.samsepiol.file.nexus.content.validation.FileContentRequestValidation;
import com.samsepiol.file.nexus.content.validation.response.ValidationResponse;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportedQueryColumnsValidation implements FileContentRequestValidation {
    private final FileSchemaConfig fileSchemaConfig;

    @Override
    public @NonNull ValidationResponse validate(@NonNull FileContentFetchServiceRequest request) throws UnsupportedFileException {
        var querySupportedColumns = fileSchemaConfig.getQuerySupportedColumns(request.getFileType());

        if (!querySupportedColumns.containsAll(request.getFilters().keySet())) {
            return ValidationResponse.invalid(Error.UNSUPPORTED_QUERY_COLUMN_FOR_FILE);
        }
        return ValidationResponse.valid();
    }

    @Override
    public @NonNull Type getType() {
        return Type.SUPPORTED_QUERY_COLUMN;
    }
}
