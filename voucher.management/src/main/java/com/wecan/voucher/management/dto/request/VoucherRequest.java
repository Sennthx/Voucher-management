package com.wecan.voucher.management.dto.request;

import com.wecan.voucher.management.model.Voucher;
import com.wecan.voucher.management.validation.EnumValidator;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public record VoucherRequest(
        @NotBlank(message = "Voucher code is required")
        @Size(min = 3, max = 20, message = "Code must be 3-20 characters")
        String code,

        @NotNull(message = "Voucher type is required")
        @EnumValidator(enumClass = Voucher.VoucherType.class, message = "Invalid voucher type. Allowed: {enumValues}")
        String type,

        @PositiveOrZero(message = "Redemption limit must be ≥ 0 (required for LIMITED)")
        Integer redemptionLimit,

        @NotNull(message = "Valid-from date is required")
        Instant validFrom,

        @FutureOrPresent(message = "Valid-to date must be in the future")
        Instant validTo,

        @NotNull(message = "Discount value is required")
        @Positive(message = "Discount value must be > 0")
        BigDecimal discountValue,

        @NotNull(message = "Discount type is required")
        @EnumValidator(enumClass = Voucher.DiscountType.class, message = "Invalid discount type. Allowed: {enumValues}")
        String discountType
) {}