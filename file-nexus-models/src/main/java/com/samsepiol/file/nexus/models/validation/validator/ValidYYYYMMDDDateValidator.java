package com.samsepiol.file.nexus.models.validation.validator;

import com.samsepiol.file.nexus.models.validation.ValidYYYYMMDDDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidYYYYMMDDDateValidator implements ConstraintValidator<ValidYYYYMMDDDate, String> {
    private static final String ZONE = "Asia/Kolkata";
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern(DATE_FORMAT).withZone(ZoneId.of(ZONE));

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
