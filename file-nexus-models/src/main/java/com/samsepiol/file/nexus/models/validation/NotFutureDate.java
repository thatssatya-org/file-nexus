package com.samsepiol.file.nexus.models.validation;


import com.samsepiol.file.nexus.models.validation.validator.NotFutureDateValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NotFutureDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotFutureDate {
    String message() default "Date must not be of future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
