package com.wecan.voucher.management.repository;

import com.wecan.voucher.management.model.Redemption;
import com.wecan.voucher.management.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByVoucher(Voucher voucher);
}