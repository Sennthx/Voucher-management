package com.wecan.voucher.management.voucherSystem.service;

import com.wecan.voucher.management.voucherSystem.model.Redemption;

import java.util.List;

public interface RedemptionService {
    Redemption redeemVoucher(String voucherCode);
    List<Redemption> getRedemptionsForVoucher(String voucherCode);
}