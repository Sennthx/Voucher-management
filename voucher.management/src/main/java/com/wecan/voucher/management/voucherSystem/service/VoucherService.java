package com.wecan.voucher.management.voucherSystem.service;

import com.wecan.voucher.management.voucherSystem.model.Voucher;

import java.util.List;
import java.util.Optional;

public interface VoucherService {
    Voucher createVoucher(Voucher voucher);
    Optional<Voucher> getByCode(String code);
    List<Voucher> getAllVouchers();
    void deleteVoucher(Long id);

}