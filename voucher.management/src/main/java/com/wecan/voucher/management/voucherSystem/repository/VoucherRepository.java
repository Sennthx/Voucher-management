package com.wecan.voucher.management.voucherSystem.repository;

import com.wecan.voucher.management.voucherSystem.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);
}