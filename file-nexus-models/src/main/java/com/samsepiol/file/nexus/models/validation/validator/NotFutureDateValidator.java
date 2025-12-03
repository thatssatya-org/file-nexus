package com.samsepiol.file.nexus.models.validation.validator;

import com.samsepiol.file.nexus.models.validation.NotFutureDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class NotFutureDateValidator implements ConstraintValidator<NotFutureDate, String> {
    private static final String ZONE = "Asia/Kolkata";
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern(DATE_FORMAT).withZone(ZoneId.of(ZONE));

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        var localDate = LocalDate.parse(date, DATE_FORMATTER);
        if (localDate.isAfter(LocalDate.now(ZoneId.of(ZONE)))) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
