package com.wecan.voucher.management.dto.request;

import com.wecan.voucher.management.validation.ValidRedemptionRequest;

// using record instead of using a POJO
@ValidRedemptionRequest
public record RedemptionRequest (

    String code
) {}