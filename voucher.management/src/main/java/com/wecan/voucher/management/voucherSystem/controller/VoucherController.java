package com.wecan.voucher.management.voucherSystem.controller;

import com.wecan.voucher.management.annotations.RateLimited;
import com.wecan.voucher.management.voucherSystem.dto.request.RedemptionRequest;
import com.wecan.voucher.management.voucherSystem.dto.request.VoucherRequest;
import com.wecan.voucher.management.voucherSystem.dto.response.RedemptionResponse;
import com.wecan.voucher.management.voucherSystem.dto.response.VoucherResponse;
import com.wecan.voucher.management.voucherSystem.model.Redemption;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import com.wecan.voucher.management.voucherSystem.service.RedemptionService;
import com.wecan.voucher.management.voucherSystem.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherService voucherService;
    private final RedemptionService redemptionService;

    public VoucherController(VoucherService voucherService, RedemptionService redemptionService) {
        this.voucherService = voucherService;
        this.redemptionService = redemptionService;
    }

    @PostMapping
    public ResponseEntity<VoucherResponse> create(@RequestBody @Valid VoucherRequest request) {
        Voucher voucher = Voucher.fromRequest(request);

        // Save the voucher and map to response DTO
        Voucher saved = voucherService.createVoucher(voucher);
        VoucherResponse response = VoucherResponse.fromEntity(saved);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @RateLimited(requests = 2, seconds = 5)
    public ResponseEntity<List<VoucherResponse>> list() {
        List<VoucherResponse> responses = voucherService.getAllVouchers().stream()
                .map(VoucherResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/redeem")
    @RateLimited(requests = 3, seconds = 5)
    public ResponseEntity<RedemptionResponse> redeemVoucher(
            @RequestBody @Valid RedemptionRequest redemptionRequest) {

        // Redeem the voucher and return the response DTO
        Redemption redemption = redemptionService.redeemVoucher(redemptionRequest.code());
        RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

        return ResponseEntity.ok(response);
    }
}
