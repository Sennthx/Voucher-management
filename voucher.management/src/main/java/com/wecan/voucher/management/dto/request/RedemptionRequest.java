package com.wecan.voucher.management.dto.request;

import com.wecan.voucher.management.validation.ValidRedemptionRequest;
import jakarta.validation.constraints.NotBlank;

// using record instead of using a POJO
@ValidRedemptionRequest
public record RedemptionRequest (

    @NotBlank(message = "Voucher code is required")
    String voucherCode

) {}