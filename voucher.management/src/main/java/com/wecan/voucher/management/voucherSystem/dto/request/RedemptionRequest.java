package com.wecan.voucher.management.voucherSystem.dto.request;

import com.wecan.voucher.management.voucherSystem.validation.ValidRedemptionRequest;

// using record instead of using a POJO
@ValidRedemptionRequest
public record RedemptionRequest (

    String code
) {}