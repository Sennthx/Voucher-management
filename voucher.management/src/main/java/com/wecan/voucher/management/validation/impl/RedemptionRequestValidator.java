package com.wecan.voucher.management.validation.impl;

import com.wecan.voucher.management.dto.request.RedemptionRequest;
import com.wecan.voucher.management.repository.VoucherRepository;
import com.wecan.voucher.management.validation.ValidRedemptionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class RedemptionRequestValidator implements ConstraintValidator<ValidRedemptionRequest, RedemptionRequest> {

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    public boolean isValid(RedemptionRequest request, ConstraintValidatorContext context) {
        if (request == null) return true; // skip null (other validations can catch it)

        context.disableDefaultConstraintViolation();

        if (request.code() == null || request.code().isBlank()) {
            context.buildConstraintViolationWithTemplate("Voucher code is required")
                    .addPropertyNode("code")
                    .addConstraintViolation();
            return false;
        }

        if (voucherRepository.findByCode(request.code()).isEmpty()) {
            context.buildConstraintViolationWithTemplate("Invalid or non-existent voucher code")
                    .addPropertyNode("code")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
