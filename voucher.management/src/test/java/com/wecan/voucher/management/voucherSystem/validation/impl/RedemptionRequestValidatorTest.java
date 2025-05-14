package com.wecan.voucher.management.voucherSystem.validation.impl;

import com.wecan.voucher.management.voucherSystem.dto.request.RedemptionRequest;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import com.wecan.voucher.management.voucherSystem.repository.VoucherRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedemptionRequestValidatorTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private RedemptionRequestValidator validator;

    private final String VALID_CODE = "VALID123";
    private final String INVALID_CODE = "INVALID123";

    @BeforeEach
    void setUp() {
        validator = new RedemptionRequestValidator(voucherRepository);
        lenient().when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(builder);
        lenient().when(builder.addPropertyNode(anyString()))
                .thenReturn(nodeBuilder);
    }

    @Test
    @DisplayName("Should accept valid redemption request")
    void isValidShouldAcceptValidRequest() {
        RedemptionRequest request = new RedemptionRequest(VALID_CODE);

        when(voucherRepository.findByCode(VALID_CODE))
                .thenReturn(Optional.of(new Voucher()));

        assertTrue(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
        verifyNoMoreInteractions(context, builder);
    }

    @Test
    @DisplayName("Should reject null request")
    void isValidShouldHandleNullRequest() {
        assertTrue(validator.isValid(null, context));
        verifyNoInteractions(context);
        verifyNoInteractions(voucherRepository);
    }

    @Test
    @DisplayName("Should reject empty code")
    void isValidShouldRejectEmptyCode() {
        RedemptionRequest request = new RedemptionRequest("");

        assertFalse(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Voucher code is required");
        verify(builder).addPropertyNode("code");
        verifyNoInteractions(voucherRepository);
    }

    @Test
    @DisplayName("Should reject blank code")
    void isValidShouldRejectBlankCode() {
        RedemptionRequest request = new RedemptionRequest("   ");

        assertFalse(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Voucher code is required");
        verify(builder).addPropertyNode("code");
        verifyNoInteractions(voucherRepository);
    }

    @Test
    @DisplayName("Should reject null code")
    void isValidShouldRejectNullCode() {
        RedemptionRequest request = new RedemptionRequest(null);

        assertFalse(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Voucher code is required");
        verify(builder).addPropertyNode("code");
        verifyNoInteractions(voucherRepository);
    }

    @Test
    @DisplayName("Should reject non-existent voucher code")
    void isValidShouldRejectInvalidCode() {
        RedemptionRequest request = new RedemptionRequest(INVALID_CODE);

        when(voucherRepository.findByCode(INVALID_CODE))
                .thenReturn(Optional.empty());

        assertFalse(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Invalid or non-existent voucher code");
        verify(builder).addPropertyNode("code");
        verify(voucherRepository).findByCode(INVALID_CODE);
    }

    @Test
    @DisplayName("Should verify repository interaction")
    void isValidShouldCheckVoucherRepository() {
        RedemptionRequest request = new RedemptionRequest(VALID_CODE);
        when(voucherRepository.findByCode(VALID_CODE))
                .thenReturn(Optional.of(new Voucher()));

        validator.isValid(request, context);
        verify(voucherRepository).findByCode(VALID_CODE);
    }
}