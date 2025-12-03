package com.samsepiol.file.nexus.exception.unchecked;

import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public abstract class FileNexusRuntimeException extends RuntimeException {
    private final Error error;
    private final Map<String, Object> context;

    protected FileNexusRuntimeException(@NonNull Error error,
                                        Map<String, Object> context) {
        super(error.getMessage());
        this.error = error;
        this.context = Objects.requireNonNullElse(context, Collections.emptyMap());
    }

    protected FileNexusRuntimeException(@NonNull Error error) {
        super(error.getMessage());
        this.error = error;
        this.context = Collections.emptyMap();
    }

    public @NonNull Integer getErrorCode() {
        return error.getCode();
    }

    public @NonNull String getDisplayMessage() {
        return error.getDisplayMessage();
    }

    public @NonNull HttpStatus getHttpStatus() {
        return error.getHttpStatus();
    }

    public static @NonNull FileNexusRuntimeException wrap(@NonNull FileNexusException exception) {
        return FileNexusRuntimeExceptionWrapper.wrap(exception.getError());
    }

    public static @NonNull FileNexusRuntimeException wrap(@NonNull FileNexusException exception,
                                                          @NonNull Map<String, Object> context) {
        return FileNexusRuntimeExceptionWrapper.wrap(exception.getError(), context);
    }

    private static class FileNexusRuntimeExceptionWrapper extends FileNexusRuntimeException {

        @Builder(access = AccessLevel.PRIVATE)
        private FileNexusRuntimeExceptionWrapper(@NonNull Error error,
                                                 Map<String, Object> context) {
            super(error, context);
        }

        public static @NonNull FileNexusRuntimeException.FileNexusRuntimeExceptionWrapper wrap(@NonNull Error error) {
           return FileNexusRuntimeExceptionWrapper.builder()
                   .error(error)
                   .build();
        }

        public static @NonNull FileNexusRuntimeException.FileNexusRuntimeExceptionWrapper wrap(@NonNull Error error,
                                                                                               @NonNull Map<String, Object> context) {
            return FileNexusRuntimeExceptionWrapper.builder()
                    .error(error)
                    .context(context)
                    .build();
        }
    }

    @Override
    public String toString() {
        return "Error: Code - " + error.getCode() + ", Message - " + error.getMessage() + ", Context: " + context;
    }
}
