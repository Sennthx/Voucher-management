package com.wecan.voucher.management.controller;

import com.wecan.voucher.management.annotations.RateLimited;
import org.springframework.web.bind.annotation.*;

public class VoucherRedemptionController {


/*    @RestController
    @RequestMapping("/api/vouchers")
    public class VoucherController {

        @RateLimited(value = 1, window = 5) // 1 requests per 5 seconds
        @PostMapping("/redeem")
        public ResponseEntity<VoucherResponse> redeemVoucher(
                @RequestBody RedeemRequest request) {
            // Your redemption logic without user tracking
            return  null;
        }

        @RateLimited
        @GetMapping("/validate/{code}")
        public ResponseEntity<VoucherValidationResponse> validateVoucher(
                @PathVariable String code) {
            // Validation logic
        }
    }*/
}
