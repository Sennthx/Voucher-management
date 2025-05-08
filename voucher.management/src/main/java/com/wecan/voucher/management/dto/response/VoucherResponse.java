package com.wecan.voucher.management.dto.response;

import com.wecan.voucher.management.model.Voucher;

import java.math.BigDecimal;
import java.time.Instant;

public record VoucherResponse(
        String code,
        String type,
        BigDecimal discountValue,
        Voucher.DiscountType discountType,
        Instant validTo,
        String status
) {}