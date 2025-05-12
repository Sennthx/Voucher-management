package com.wecan.voucher.management.dto.response;

import com.wecan.voucher.management.model.Redemption;
import java.time.LocalDate;

public record RedemptionResponse(
        Long redemptionId,
        LocalDate redeemedAt,
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