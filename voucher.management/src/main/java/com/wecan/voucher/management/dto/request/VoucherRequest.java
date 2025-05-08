package com.wecan.voucher.management.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

public class VoucherRequest {
    public String code;
    public BigDecimal discountValue;
    public String type; // SINGLE, MULTIPLE, LIMITED
    public String discountType; // FIXED, PERCENTAGE
    public Integer redemptionLimit;
    public Instant validFrom;
    public Instant validTo;
}
