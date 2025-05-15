package com.wecan.voucher.management.voucherSystem.validation.impl;

import com.wecan.voucher.management.voucherSystem.dto.request.VoucherRequest;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherRequestValidatorTest {

    private VoucherRequestValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new VoucherRequestValidator();
        lenient().when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(builder);
        lenient().when(builder.addPropertyNode(anyString()))
                .thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
    }

    private VoucherRequest createValidRequest(Voucher.VoucherType type) {
        return new VoucherRequest(
                "TEST123",
                type.name(),
                type == Voucher.VoucherType.LIMITED ? 10 : null,
                type == Voucher.VoucherType.LIMITED ? Instant.now() : null,
                type == Voucher.VoucherType.LIMITED ? Instant.now().plusSeconds(30 * 24 * 60 * 60) : null,
                10,
                Voucher.DiscountType.PERCENTAGE.name()
        );
    }

    @Test
    @DisplayName("Should return true for valid SINGLE voucher request")
    void isValidShouldReturnTrueForValidRequest() {
        VoucherRequest request = createValidRequest(Voucher.VoucherType.SINGLE);
        assertTrue(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
        verifyNoMoreInteractions(context, builder);
    }

    @Test
    @DisplayName("Should validate LIMITED type requirements")
    void isValidShouldValidateLimitedTypeRequirements() {
        VoucherRequest request = new VoucherRequest(
                "TEST123",
                Voucher.VoucherType.LIMITED.name(),
                null,
                null,
                null,
                10,
                Voucher.DiscountType.PERCENTAGE.name()
        );

        assertFalse(validator.isValid(request, context));
        verify(context).buildConstraintViolationWithTemplate("Redemption limit is required for type: LIMITED");
        verify(context).buildConstraintViolationWithTemplate("validFrom is required for LIMITED vouchers");
        verify(context).buildConstraintViolationWithTemplate("validTo is required for LIMITED vouchers");
    }

    @Test
    @DisplayName("Should validate redemption limit > 1 for LIMITED type")
    void isValidShouldValidateRedemptionLimitForLimitedType() {
        VoucherRequest request = new VoucherRequest(
                "TEST123",
                Voucher.VoucherType.LIMITED.name(),
                1,
                Instant.now(),
                Instant.now().plusSeconds(30 * 24 * 60 * 60),
                10,
                Voucher.DiscountType.PERCENTAGE.name()
        );

        assertFalse(validator.isValid(request, context));
        verify(context).buildConstraintViolationWithTemplate("Redemption limit must be greater than 1 for type: LIMITED");
    }

    @Test
    @DisplayName("Should validate validTo date is after validFrom date")
    void isValidShouldValidateDateRange() {
        VoucherRequest request = new VoucherRequest(
                "TEST123",
                Voucher.VoucherType.SINGLE.name(),
                null,
                Instant.now(),
                Instant.now().minusSeconds(86400),
                10,
                Voucher.DiscountType.PERCENTAGE.name()
        );

        assertFalse(validator.isValid(request, context));
        verify(context).buildConstraintViolationWithTemplate("Valid-to date must be after or equal to valid-from date");
    }

    @Test
    @DisplayName("Should validate percentage discount ≤ 100")
    void isValidShouldValidatePercentageDiscount() {
        VoucherRequest request = new VoucherRequest(
                "TEST123",
                Voucher.VoucherType.SINGLE.name(),
                null,
                Instant.now(),
                Instant.now().plusSeconds(30 * 24 * 60 * 60),
                101,
                Voucher.DiscountType.PERCENTAGE.name()
        );

        assertFalse(validator.isValid(request, context));
        verify(context).buildConstraintViolationWithTemplate("Percentage discount cannot be greater than 100%");
    }

    @Test
    @DisplayName("Should handle null request")
    void isValidShouldHandleNullRequest() {
        assertTrue(validator.isValid(null, context));
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("Should validate MULTIPLE type requirements")
    void isValidShouldValidateMultipleTypeRequirements() {
        VoucherRequest request = new VoucherRequest(
                "TEST123",
                Voucher.VoucherType.MULTIPLE.name(),
                null,
                null,
                null,
                10,
                Voucher.DiscountType.PERCENTAGE.name()
        );

        assertFalse(validator.isValid(request, context));
        verify(context).buildConstraintViolationWithTemplate("Redemption limit is required for type: MULTIPLE");
    }

    @Test
    @DisplayName("Should accept valid FIXED amount discount")
    void isValidShouldAcceptFixedAmountDiscount() {
        VoucherRequest request = new VoucherRequest(
                "TEST123",
                Voucher.VoucherType.SINGLE.name(),
                null,
                null,
                null,
                500,
                Voucher.DiscountType.FIXED.name()
        );

        assertTrue(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
        verifyNoMoreInteractions(context, builder);
    }

    @Test
    @DisplayName("Should not require dates for non-LIMITED types")
    void isValidShouldNotRequireDatesForNonLimitedTypes() {
        VoucherRequest request = createValidRequest(Voucher.VoucherType.SINGLE);
        assertTrue(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
        verifyNoMoreInteractions(context, builder);
    }
}