package com.wecan.voucher.management.dto.response;

import com.wecan.voucher.management.model.Voucher;
import java.math.BigDecimal;
import java.time.LocalDate;

public record VoucherResponse(
        String code,
        String type,
        BigDecimal discountValue,
        Voucher.DiscountType discountType,
        LocalDate validTo,
        String status
) {}