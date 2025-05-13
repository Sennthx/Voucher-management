package com.wecan.voucher.management.voucherSystem.repository;

import com.wecan.voucher.management.voucherSystem.model.Redemption;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByVoucher(Voucher voucher);
    long countByVoucherId(Long voucherId);
}