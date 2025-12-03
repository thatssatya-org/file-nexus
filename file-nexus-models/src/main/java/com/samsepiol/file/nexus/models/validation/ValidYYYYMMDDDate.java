package com.samsepiol.file.nexus.models.validation;


import com.samsepiol.file.nexus.models.validation.validator.ValidYYYYMMDDDateValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidYYYYMMDDDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYYYYMMDDDate {
    String message() default "Date must be in YYYY-MM-DD format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
