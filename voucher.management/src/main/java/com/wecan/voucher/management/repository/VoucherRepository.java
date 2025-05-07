package com.wecan.voucher.management.repository;

import com.wecan.voucher.management.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    @Query("SELECT COUNT(r) FROM Redemption r WHERE r.voucher.id = :voucherId")
    long countRedemptions(Long voucherId);
}