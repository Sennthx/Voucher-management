package com.wecan.voucher.management.dto.response;

import java.time.Instant;

public record RedemptionResponse(
        Long id,
        Instant redeemedAt,
        String redeemerIp
) {}