    package com.wecan.voucher.management.controller;

    import com.wecan.voucher.management.annotations.RateLimited;
    import com.wecan.voucher.management.dto.request.RedemptionRequest;
    import com.wecan.voucher.management.dto.request.VoucherRequest;
    import com.wecan.voucher.management.dto.response.RedemptionResponse;
    import com.wecan.voucher.management.dto.response.VoucherValidationResponse;
    import com.wecan.voucher.management.model.Redemption;
    import com.wecan.voucher.management.model.Voucher;
    import com.wecan.voucher.management.service.VoucherService;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.validation.Valid;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/vouchers")
    public class VoucherController {

        private final VoucherService voucherService;

        public VoucherController(VoucherService voucherService) {
            this.voucherService = voucherService;
        }

        @PostMapping
        public ResponseEntity<Voucher> create(@RequestBody @Valid VoucherRequest request) {
            Voucher voucher = new Voucher(
                    request.code(),
                    Voucher.VoucherType.valueOf(request.type()),
                    request.redemptionLimit(),
                    request.validFrom(),
                    request.validTo(),
                    request.discountValue(),
                    Voucher.DiscountType.valueOf(request.discountType())
            );
            return ResponseEntity.ok(voucherService.createVoucher(voucher));
        }

        @GetMapping
        @RateLimited(requests = 1, seconds = 5)
        public ResponseEntity<List<Voucher>> list() {
            return ResponseEntity.ok(voucherService.getAllVouchers());
        }

        @PostMapping("/redeem")
        @RateLimited(requests = 1, seconds = 5)
        public ResponseEntity<RedemptionResponse> redeemVoucher(
                @RequestBody @Valid RedemptionRequest redemptionRequest,
                HttpServletRequest request) {

            Redemption redemption = voucherService.redeemVoucher(
                    redemptionRequest,
                    request.getRemoteAddr()
            );

            return ResponseEntity.ok(new RedemptionResponse(
                    redemption.getId(),
                    redemption.getRedeemedAt(),
                    redemption.getRedeemerIp()
            ));
        }

        @GetMapping("/validate/{code}")
        public ResponseEntity<VoucherValidationResponse> validateVoucher(
                @PathVariable String code) {
            // No changes needed - public endpoint
            return ResponseEntity.ok(voucherService.validateVoucher(code));
        }

    }
