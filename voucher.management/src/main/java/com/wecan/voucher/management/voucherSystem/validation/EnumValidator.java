package com.wecan.voucher.management.voucherSystem.validation;

import com.wecan.voucher.management.voucherSystem.validation.impl.EnumValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidatorImpl.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClass();
    String message() default "Invalid value. Allowed values: {enumValues}";
    boolean ignoreCase() default false;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}