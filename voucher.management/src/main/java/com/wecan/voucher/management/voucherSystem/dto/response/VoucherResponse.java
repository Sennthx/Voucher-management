package com.wecan.voucher.management.voucherSystem.dto.response;

import com.wecan.voucher.management.voucherSystem.model.Voucher;

import java.time.Instant;

public record VoucherResponse(
        String code,
        String type,
        Integer redemptionLimit,
        Integer discountValue,
        Voucher.DiscountType discountType,
        Instant validTo,
        String status
) {

    public static VoucherResponse fromEntity(Voucher voucher) {
        return new VoucherResponse(
                voucher.getCode(),
                voucher.getType().name(),
                voucher.getRedemptionLimit(),
                voucher.getDiscountValue(),
                voucher.getDiscountType(),
                voucher.getValidTo(),
                determineStatus(voucher) // Determine the status based on dates
        );
    }

    private static String determineStatus(Voucher voucher) {
        Instant today = Instant.now();
        if (voucher.getValidFrom() != null && today.isBefore(voucher.getValidFrom())) {
            return "NOT_YET_VALID";
        }
        if (voucher.getValidTo() != null && today.isAfter(voucher.getValidTo())) {
            return "EXPIRED";
        }
        return "ACTIVE";
    }
}
