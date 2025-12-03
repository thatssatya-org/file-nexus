package com.samsepiol.file.nexus.content.validation.response;

import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class ValidationResponse {

    @NonNull
    Boolean valid;
    Error error;

    public boolean isInvalid() {
        return !valid;
    }

    public static ValidationResponse invalid(Error error) {
        return ValidationResponse.builder().valid(Boolean.FALSE).error(error).build();
    }

    public static ValidationResponse valid() {
        return ValidationResponse.builder().valid(Boolean.TRUE).build();
    }
}
