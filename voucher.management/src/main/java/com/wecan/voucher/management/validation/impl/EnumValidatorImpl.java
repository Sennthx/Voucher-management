package com.wecan.voucher.management.validation.impl;

import com.wecan.voucher.management.validation.EnumValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {
    private List<String> enumValues;
    private boolean ignoreCase;

    @Override
    public void initialize(EnumValidator constraint) {
        this.enumValues = Arrays.stream(constraint.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
        this.ignoreCase = constraint.ignoreCase();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (!isValidEnum(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate()
                            .replace("{enumValues}", String.join(", ", enumValues))
            ).addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isValidEnum(String value) {
        return ignoreCase
                ? enumValues.stream().anyMatch(v -> v.equalsIgnoreCase(value))
                : enumValues.contains(value);
    }
}