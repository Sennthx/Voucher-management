package com.wecan.voucher.management.voucherSystem.validation;

import com.wecan.voucher.management.voucherSystem.validation.impl.RedemptionRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RedemptionRequestValidator.class)
@Target({ ElementType.TYPE }) // Applies at the class (record/object) level
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRedemptionRequest {
    String message() default "Invalid voucher redemption request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}