package com.wecan.voucher.management.service.impl;

import com.wecan.voucher.management.dto.request.RedemptionRequest;
import com.wecan.voucher.management.dto.response.VoucherValidationResponse;
import com.wecan.voucher.management.exception.DuplicateResourceException;
import com.wecan.voucher.management.model.Redemption;
import com.wecan.voucher.management.model.Voucher;
import com.wecan.voucher.management.repository.VoucherRepository;
import com.wecan.voucher.management.service.VoucherService;
import org.springframework.stereotype.Service;

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

        if (voucher.getType() == Voucher.VoucherType.SINGLE) {
            voucher.setRedemptionLimit(1);
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


    @Override
    public Redemption redeemVoucher(RedemptionRequest redemptionRequest, String remoteAddr) {
        return null;
    }

}