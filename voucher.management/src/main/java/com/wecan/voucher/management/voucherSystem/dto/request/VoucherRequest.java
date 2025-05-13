package com.wecan.voucher.management.voucherSystem.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import com.wecan.voucher.management.voucherSystem.validation.EnumValidator;
import com.wecan.voucher.management.voucherSystem.validation.ValidVoucherRequest;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@ValidVoucherRequest
public record VoucherRequest(

        @NotBlank(message = "Voucher code is required")
        @Size(min = 3, max = 20, message = "Code must be 3-20 characters")
        @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Code can only contain letters, numbers, dashes, and underscores")
        String code,

        @NotNull(message = "Voucher type is required")
        @EnumValidator(enumClass = Voucher.VoucherType.class, message = "Invalid voucher type. Allowed: {enumValues}")
        String type,

        @PositiveOrZero(message = "Redemption limit must be ≥ 0")
        Integer redemptionLimit,

        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "Valid-from date is required")
        LocalDate validFrom,

        @JsonFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent(message = "Valid-to date must be in the future")
        LocalDate validTo,

        @NotNull(message = "Discount value is required")
        @Positive(message = "Discount value must be > 0")
        Integer discountValue,

        @NotNull(message = "Discount type is required")
        @EnumValidator(enumClass = Voucher.DiscountType.class, message = "Invalid discount type. Allowed: {enumValues}")
        String discountType
) {}