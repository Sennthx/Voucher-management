package com.wecan.voucher.management.voucherSystem.controller;

import com.wecan.voucher.management.voucherSystem.dto.request.RedemptionRequest;
import com.wecan.voucher.management.voucherSystem.dto.request.VoucherRequest;
import com.wecan.voucher.management.voucherSystem.dto.response.RedemptionResponse;
import com.wecan.voucher.management.voucherSystem.dto.response.VoucherResponse;
import com.wecan.voucher.management.voucherSystem.model.Redemption;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import com.wecan.voucher.management.voucherSystem.service.RedemptionService;
import com.wecan.voucher.management.voucherSystem.service.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherControllerTest {

    @Mock
    private VoucherService voucherService;

    @InjectMocks
    private VoucherController voucherController;

    private Voucher testVoucher;
    private Redemption testRedemption;
    private final String TEST_CODE = "TEST_123";
    private final LocalDate TODAY = LocalDate.now();
    private final LocalDate FUTURE_DATE = TODAY.plusDays(30);
    private final LocalDate PAST_DATE = TODAY.minusDays(1);

    @BeforeEach
    void setUp() {
        testVoucher = new Voucher();
        testVoucher.setId(1L);
        testVoucher.setCode(TEST_CODE);
        testVoucher.setType(Voucher.VoucherType.MULTIPLE);
        testVoucher.setRedemptionLimit(100);
        testVoucher.setValidFrom(TODAY.minusDays(1));
        testVoucher.setValidTo(FUTURE_DATE);
        testVoucher.setDiscountValue(10);
        testVoucher.setDiscountType(Voucher.DiscountType.PERCENTAGE);

        testRedemption = new Redemption();
        testRedemption.setId(1L);
        testRedemption.setVoucher(testVoucher);
        testRedemption.setRedeemedAt(TODAY);
    }

    @Test
    @DisplayName("Creating voucher should return response with all fields and correct status")
    void createVoucherShouldReturnCompleteResponse() {
        VoucherRequest request = new VoucherRequest(
                TEST_CODE,
                "MULTIPLE",
                100,
                TODAY.minusDays(1),
                FUTURE_DATE,
                10,
                "PERCENTAGE"
        );
        when(voucherService.createVoucher(any(Voucher.class))).thenReturn(testVoucher);

        ResponseEntity<VoucherResponse> response = voucherController.create(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        VoucherResponse body = response.getBody();
        assertNotNull(body);

        assertEquals(TEST_CODE, body.code());
        assertEquals("MULTIPLE", body.type());
        assertEquals(100, body.redemptionLimit());
        assertEquals(10, body.discountValue());
        assertEquals(Voucher.DiscountType.PERCENTAGE, body.discountType());
        assertEquals(FUTURE_DATE, body.validTo());
        assertEquals("ACTIVE", body.status());

        verify(voucherService).createVoucher(any(Voucher.class));
    }

    @Test
    @DisplayName("Listing vouchers should return all fields including status")
    void listVouchersShouldIncludeStatus() {
        when(voucherService.getAllVouchers()).thenReturn(List.of(testVoucher));

        ResponseEntity<List<VoucherResponse>> response = voucherController.list();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<VoucherResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());

        VoucherResponse voucherResponse = body.get(0);
        assertEquals("ACTIVE", voucherResponse.status());
    }


    @Test
    @DisplayName("Response should show NOT_YET_VALID status when validFrom is in future")
    void responseShouldShowNotYetValidStatus() {
        testVoucher.setValidFrom(TODAY.plusDays(1));
        when(voucherService.getAllVouchers()).thenReturn(List.of(testVoucher));

        ResponseEntity<List<VoucherResponse>> response = voucherController.list();

        assertEquals("NOT_YET_VALID", response.getBody().get(0).status());
    }

    @Test
    @DisplayName("Response should show EXPIRED status when validTo is in past")
    void responseShouldShowExpiredStatus() {
        testVoucher.setValidTo(PAST_DATE);
        when(voucherService.getAllVouchers()).thenReturn(List.of(testVoucher));

        ResponseEntity<List<VoucherResponse>> response = voucherController.list();

        assertEquals("EXPIRED", response.getBody().get(0).status());
    }

    @Test
    @DisplayName("Response should show ACTIVE status when current date is within valid range")
    void responseShouldShowActiveStatus() {
        testVoucher.setValidFrom(PAST_DATE);
        testVoucher.setValidTo(FUTURE_DATE);
        when(voucherService.getAllVouchers()).thenReturn(List.of(testVoucher));

        ResponseEntity<List<VoucherResponse>> response = voucherController.list();

        assertEquals("ACTIVE", response.getBody().get(0).status());
    }

    @Test
    @DisplayName("Fixed amount voucher should return correct response")
    void fixedAmountVoucherShouldReturnCorrectResponse() {
        Voucher fixedVoucher = new Voucher();
        fixedVoucher.setDiscountType(Voucher.DiscountType.FIXED);
        fixedVoucher.setDiscountValue(500);
        fixedVoucher.setValidFrom(PAST_DATE);
        fixedVoucher.setValidTo(FUTURE_DATE);

        VoucherRequest request = new VoucherRequest(
                "FIXED_123",
                "SINGLE",
                1,
                PAST_DATE,
                FUTURE_DATE,
                500,
                "FIXED"
        );
        when(voucherService.createVoucher(any(Voucher.class))).thenReturn(fixedVoucher);

        ResponseEntity<VoucherResponse> response = voucherController.create(request);

        assertEquals(Voucher.DiscountType.FIXED, response.getBody().discountType());
        assertEquals(500, response.getBody().discountValue());
        assertEquals("ACTIVE", response.getBody().status());
    }
}