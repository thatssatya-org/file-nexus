package com.samsepiol.file.nexus.exception.checked;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
public abstract class FileNexusException extends Exception {
    private final Error error;

    protected FileNexusException(@NonNull Error error) {
        super(error.getMessage());
        this.error = error;
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

    @Override
    public String toString() {
        return "Error: Code - " + error.getCode() + ", Message - " + error.getMessage();
    }
}
