package com.wecan.voucher.management.voucherSystem.service.impl;

import com.wecan.voucher.management.exception.DuplicateResourceException;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import com.wecan.voucher.management.voucherSystem.repository.VoucherRepository;
import com.wecan.voucher.management.voucherSystem.service.VoucherService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherServiceImpl(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public Voucher createVoucher(Voucher voucher) {
        if (voucherRepository.findByCode(voucher.getCode()).isPresent()) {
            throw new DuplicateResourceException("code", "Voucher with this code already exists");
        }

        if (voucher.getType() == null) {
            throw new IllegalArgumentException("Voucher type cannot be null");
        }

        if (voucher.getDiscountType() == null) {
            throw new IllegalArgumentException("Discount type cannot be null");
        }

        if (voucher.getDiscountValue() == null) {
            throw new IllegalArgumentException("Discount value cannot be null");
        }

        if (voucher.getDiscountType() == Voucher.DiscountType.FIXED &&
                voucher.getDiscountValue() != null && voucher.getDiscountValue() < 0) {
            throw new IllegalArgumentException("Fixed amount discount cannot be negative");
        }

        if (voucher.getType() == Voucher.VoucherType.SINGLE) {
            voucher.setRedemptionLimit(1);
        }

        if (voucher.getType() == Voucher.VoucherType.LIMITED || voucher.getType() == Voucher.VoucherType.MULTIPLE) {
            if (voucher.getRedemptionLimit() == null || voucher.getRedemptionLimit() <= 1) {
                throw new IllegalArgumentException("Redemption limit must be greater than 1 for type: " + voucher.getType());
            }
        }

        if (voucher.getType() == Voucher.VoucherType.LIMITED) {
            if (voucher.getValidFrom() == null || voucher.getValidTo() == null) {
                throw new IllegalArgumentException("Valid-from and valid-to are required for LIMITED vouchers");
            }
        }

        if (voucher.getValidFrom() != null && voucher.getValidTo() != null &&
                voucher.getValidTo().isBefore(voucher.getValidFrom())) {
            throw new IllegalArgumentException("Valid-to date must be after or equal to valid-from date");
        }

        if (voucher.getValidTo() != null && voucher.getValidTo().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Valid-to date cannot be in the past");
        }

        if (voucher.getDiscountType() == Voucher.DiscountType.PERCENTAGE &&
                voucher.getDiscountValue() != null && voucher.getDiscountValue() > 100) {
            throw new IllegalArgumentException("Percentage discount cannot be greater than 100%");
        }

        return voucherRepository.save(voucher);
    }

    @Override
    public Optional<Voucher> getByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    @Override
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    @Override
    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }
}