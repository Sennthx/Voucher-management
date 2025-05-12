package com.wecan.voucher.management.dto.response;

import java.time.LocalDate;

public record RedemptionResponse(
        Long redemptionId,
        LocalDate redeemedAt,
        String redeemerIp,
        Integer discountValue,
        String discountType
) {}