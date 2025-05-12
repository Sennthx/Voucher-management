package com.wecan.voucher.management.service;

import com.wecan.voucher.management.model.Redemption;

import java.util.List;

public interface RedemptionService {
    Redemption redeemVoucher(String voucherCode);
    List<Redemption> getRedemptionsForVoucher(String voucherCode);
}