package com.wecan.voucher.management.voucherSystem.validation.impl;

import com.wecan.voucher.management.voucherSystem.validation.EnumValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnumValidatorImplTest {

    private EnumValidatorImpl validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private EnumValidator constraintAnnotation;

    private enum TestEnum {
        VALUE1, VALUE2, VALUE3
    }

    @BeforeEach
    void setUp() {
        validator = new EnumValidatorImpl();
        // Mark context as lenient
        Mockito.lenient().when(context.getDefaultConstraintMessageTemplate())
                .thenReturn("Invalid value. Allowed values: {enumValues}");
        Mockito.lenient().when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(builder);

        when(constraintAnnotation.enumClass()).thenReturn((Class) TestEnum.class);
    }

    @Test
    @DisplayName("Should initialize validator with enum values")
    void initializeShouldSetEnumValues() {
        validator.initialize(constraintAnnotation);

        List<String> expectedValues = Arrays.asList("VALUE1", "VALUE2", "VALUE3");
        assertTrue(validator.getEnumValues().containsAll(expectedValues));
    }

    @Test
    @DisplayName("Should accept null value")
    void isValidShouldAcceptNullValue() {
        validator.initialize(constraintAnnotation);
        assertTrue(validator.isValid(null, context));
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should validate case-sensitive enum values")
    void isValidShouldValidateCaseSensitive() {
        when(constraintAnnotation.ignoreCase()).thenReturn(false);
        validator.initialize(constraintAnnotation);

        assertTrue(validator.isValid("VALUE1", context));
        assertFalse(validator.isValid("value1", context));
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    @DisplayName("Should validate case-insensitive enum values")
    void isValidShouldValidateCaseInsensitive() {
        when(constraintAnnotation.ignoreCase()).thenReturn(true);
        validator.initialize(constraintAnnotation);

        assertTrue(validator.isValid("VALUE1", context));
        assertTrue(validator.isValid("value1", context));
        assertFalse(validator.isValid("invalid", context));
        verify(context, times(1)).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    @DisplayName("Should build proper error message")
    void isValidShouldBuildErrorMessage() {
        when(constraintAnnotation.ignoreCase()).thenReturn(false);
        validator.initialize(constraintAnnotation);

        when(context.getDefaultConstraintMessageTemplate())
                .thenReturn("Invalid value. Allowed values: {enumValues}");

        validator.isValid("INVALID", context);

        verify(context).buildConstraintViolationWithTemplate(
                "Invalid value. Allowed values: VALUE1, VALUE2, VALUE3"
        );
        verify(builder).addConstraintViolation();
    }

    @Test
    @DisplayName("Should handle empty enum class")
    void initializeShouldHandleEmptyEnum() {
        when(constraintAnnotation.enumClass()).thenReturn((Class) EmptyEnum.class);
        validator.initialize(constraintAnnotation);

        assertTrue(validator.getEnumValues().isEmpty());
        assertFalse(validator.isValid("ANY", context));
    }

    private enum EmptyEnum {
        // No values
    }

    private List<String> getEnumValues() {
        return validator.getEnumValues();
    }
}