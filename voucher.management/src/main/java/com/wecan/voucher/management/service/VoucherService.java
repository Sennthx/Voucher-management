package com.wecan.voucher.management.service;

import com.wecan.voucher.management.dto.request.RedemptionRequest;
import com.wecan.voucher.management.dto.response.VoucherValidationResponse;
import com.wecan.voucher.management.model.Redemption;
import com.wecan.voucher.management.model.Voucher;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface VoucherService {
    Voucher createVoucher(Voucher voucher);
    Optional<Voucher> getByCode(String code);
    List<Voucher> getAllVouchers();
    void deleteVoucher(Long id);

    Redemption redeemVoucher(@Valid RedemptionRequest redemptionRequest, String remoteAddr);

    // More: update, redeem, validate, etc.
}