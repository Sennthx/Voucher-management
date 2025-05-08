package com.wecan.voucher.management.dto.response;

import com.wecan.voucher.management.model.Voucher.DiscountType;
import java.math.BigDecimal;
import java.time.Instant;

public record VoucherValidationResponse(
        String code,
        boolean isValid,
        String message,
        BigDecimal discountValue,
        DiscountType discountType,
        Instant validTo
) {}