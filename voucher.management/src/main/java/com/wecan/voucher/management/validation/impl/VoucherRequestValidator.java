package com.wecan.voucher.management.validation.impl;

import com.wecan.voucher.management.dto.request.VoucherRequest;
import com.wecan.voucher.management.validation.ValidVoucherRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VoucherRequestValidator implements ConstraintValidator<ValidVoucherRequest, VoucherRequest> {

    @Override
    public boolean isValid(VoucherRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        String type = request.type();
        Integer redemptionLimit = request.redemptionLimit();

        if ("LIMITED".equalsIgnoreCase(type) || "MULTIPLE".equalsIgnoreCase(type)) {
            if (redemptionLimit == null) {
                context.buildConstraintViolationWithTemplate(
                                "Redemption limit is required for type: " + type)
                        .addPropertyNode("redemptionLimit")
                        .addConstraintViolation();
                valid = false;
            } else if (redemptionLimit <= 1) {
                context.buildConstraintViolationWithTemplate(
                                "Redemption limit must be greater than 1 for type: " + type)
                        .addPropertyNode("redemptionLimit")
                        .addConstraintViolation();
                valid = false;
            }
        }

        if ("LIMITED".equalsIgnoreCase(type)) {
            if (request.validFrom() == null) {
                context.buildConstraintViolationWithTemplate(
                                "validFrom is required for LIMITED vouchers")
                        .addPropertyNode("validFrom")
                        .addConstraintViolation();
                valid = false;
            }
            if (request.validTo() == null) {
                context.buildConstraintViolationWithTemplate(
                                "validTo is required for LIMITED vouchers")
                        .addPropertyNode("validTo")
                        .addConstraintViolation();
                valid = false;
            }
        }

        if (request.validFrom() != null && request.validTo() != null &&
                request.validTo().isBefore(request.validFrom())) {
            context.buildConstraintViolationWithTemplate(
                            "Valid-to date must be after or equal to valid-from date")
                    .addPropertyNode("validTo")
                    .addConstraintViolation();
            valid = false;
        }

        String discountType = request.discountType();
        Integer discountValue = request.discountValue();
        if ("PERCENTAGE".equalsIgnoreCase(discountType) && discountValue != null && discountValue > 100) {
            context.buildConstraintViolationWithTemplate(
                            "Percentage discount cannot be greater than 100%")
                    .addPropertyNode("discountValue")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }

}
