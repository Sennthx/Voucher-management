package com.wecan.voucher.management.service;

import com.wecan.voucher.management.model.Redemption;

import java.util.List;

public interface RedemptionService {
    Redemption redeemVoucher(Long voucherId, String ip, String userAgent);
    List<Redemption> getRedemptionsForVoucher(Long voucherId);
}