package com.wecan.voucher.management.voucherSystem.validation;

import com.wecan.voucher.management.voucherSystem.validation.impl.VoucherRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VoucherRequestValidator.class)
@Target({ ElementType.TYPE }) // Apply to class-level objects (like a request DTO)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVoucherRequest {
    String message() default "Invalid voucher configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
