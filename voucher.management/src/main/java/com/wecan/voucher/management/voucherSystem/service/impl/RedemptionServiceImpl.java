package com.wecan.voucher.management.voucherSystem.service.impl;

import com.wecan.voucher.management.exception.ResourceNotFoundException;
import com.wecan.voucher.management.voucherSystem.model.Redemption;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import com.wecan.voucher.management.voucherSystem.repository.RedemptionRepository;
import com.wecan.voucher.management.voucherSystem.repository.VoucherRepository;
import com.wecan.voucher.management.voucherSystem.service.RedemptionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class RedemptionServiceImpl implements RedemptionService {

    private final RedemptionRepository redemptionRepository;
    private final VoucherRepository voucherRepository;

    public RedemptionServiceImpl(RedemptionRepository redemptionRepository, VoucherRepository voucherRepository) {
        this.redemptionRepository = redemptionRepository;
        this.voucherRepository = voucherRepository;
    }

    @Override
    public Redemption redeemVoucher(String voucherCode) {
        Voucher voucher = voucherRepository.findByCode(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException("voucher", "Voucher not found"));

        Instant today = Instant.now();
        if (voucher.getValidFrom().isAfter(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher is not yet valid");
        }

        if (voucher.getValidTo() != null && voucher.getValidTo().isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher has expired");
        }

        long usageCount = redemptionRepository.countByVoucherId(voucher.getId());
        if (voucher.getRedemptionLimit() != null && usageCount >= voucher.getRedemptionLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher redemption limit reached");
        }

        Redemption redemption = new Redemption(today, voucher);
        return redemptionRepository.save(redemption);
    }

    @Override
    public List<Redemption> getRedemptionsForVoucher(String voucherCode) {
        Voucher voucher = voucherRepository.findByCode(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException("voucher", "Voucher not found"));

        return redemptionRepository.findByVoucher(voucher);
    }
}