package com.wecan.voucher.management.service.impl;

import com.wecan.voucher.management.exception.ResourceNotFoundException;
import com.wecan.voucher.management.model.Redemption;
import com.wecan.voucher.management.model.Voucher;
import com.wecan.voucher.management.repository.RedemptionRepository;
import com.wecan.voucher.management.repository.VoucherRepository;
import com.wecan.voucher.management.service.RedemptionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public Redemption redeemVoucher(Long voucherId, String ip, String userAgent) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("voucher", "Voucher not found"));

        Redemption redemption = new Redemption(Instant.now(), ip, voucher);
        return redemptionRepository.save(redemption);
    }

    @Override
    public List<Redemption> getRedemptionsForVoucher(Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("voucher", "Voucher not found"));

        return redemptionRepository.findByVoucher(voucher);
    }
}