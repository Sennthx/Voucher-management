package com.wecan.voucher.management.voucherSystem.dto.response;

import com.wecan.voucher.management.voucherSystem.model.Redemption;

import java.time.Instant;

public record RedemptionResponse(
        Long redemptionId,
        Instant redeemedAt,
        Integer discountValue,
        String discountType
) {
    public static RedemptionResponse fromEntity(Redemption redemption) {
        var voucher = redemption.getVoucher();

        return new RedemptionResponse(
                redemption.getId(),
                redemption.getRedeemedAt(),
                voucher.getDiscountValue(),
                voucher.getDiscountType().name()
        );
    }
}