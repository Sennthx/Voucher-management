package com.wecan.voucher.management.voucherSystem.service.impl;

import com.wecan.voucher.management.exception.ResourceNotFoundException;
import com.wecan.voucher.management.voucherSystem.model.Redemption;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import com.wecan.voucher.management.voucherSystem.repository.RedemptionRepository;
import com.wecan.voucher.management.voucherSystem.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedemptionServiceImplTest {

    @Mock
    private RedemptionRepository redemptionRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @InjectMocks
    private RedemptionServiceImpl redemptionService;

    private Voucher validVoucher;
    private final String TEST_VOUCHER_CODE = "TEST123";
    private final Instant NOW = Instant.now();
    private final Instant YESTERDAY = NOW.minus(1, ChronoUnit.DAYS);
    private final Instant THIRTY_DAYS_LATER = NOW.plus(30, ChronoUnit.DAYS);

    @BeforeEach
    void setUp() {
        validVoucher = new Voucher();
        validVoucher.setId(1L);
        validVoucher.setCode(TEST_VOUCHER_CODE);
        validVoucher.setType(Voucher.VoucherType.LIMITED);
        validVoucher.setRedemptionLimit(5);
        validVoucher.setValidFrom(YESTERDAY);
        validVoucher.setValidTo(THIRTY_DAYS_LATER);
        validVoucher.setDiscountType(Voucher.DiscountType.PERCENTAGE);
        validVoucher.setDiscountValue(20);
    }

    @Test
    @DisplayName("Should successfully redeem valid voucher")
    void redeemVoucherShouldSucceedForValidVoucher() {
        when(voucherRepository.findByCode(TEST_VOUCHER_CODE)).thenReturn(Optional.of(validVoucher));
        when(redemptionRepository.countByVoucherId(validVoucher.getId())).thenReturn(0L);

        Redemption expectedRedemption = new Redemption(NOW, validVoucher);
        when(redemptionRepository.save(any(Redemption.class))).thenReturn(expectedRedemption);

        Redemption result = redemptionService.redeemVoucher(TEST_VOUCHER_CODE);

        assertNotNull(result);
        assertEquals(NOW, result.getRedeemedAt());
        assertEquals(validVoucher, result.getVoucher());
        verify(redemptionRepository).save(any(Redemption.class));
    }

    @Test
    @DisplayName("Should throw when voucher not found")
    void redeemVoucherShouldThrowWhenVoucherNotFound() {
        when(voucherRepository.findByCode("INVALID_CODE")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> redemptionService.redeemVoucher("INVALID_CODE")
        );
        assertEquals("Voucher not found", ex.getMessage());
        verify(redemptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when voucher is not yet valid")
    void redeemVoucherShouldThrowWhenVoucherNotValid() {
        validVoucher.setValidFrom(NOW.plus(1, ChronoUnit.DAYS));
        when(voucherRepository.findByCode(TEST_VOUCHER_CODE)).thenReturn(Optional.of(validVoucher));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> redemptionService.redeemVoucher(TEST_VOUCHER_CODE)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Voucher is not yet valid", ex.getReason());
        verify(redemptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when voucher has expired")
    void redeemVoucherShouldThrowWhenVoucherExpired() {
        validVoucher.setValidTo(NOW.minus(1, ChronoUnit.DAYS));
        when(voucherRepository.findByCode(TEST_VOUCHER_CODE)).thenReturn(Optional.of(validVoucher));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> redemptionService.redeemVoucher(TEST_VOUCHER_CODE)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Voucher has expired", ex.getReason());
        verify(redemptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when redemption limit reached")
    void redeemVoucherShouldThrowWhenRedemptionLimitReached() {
        when(voucherRepository.findByCode(TEST_VOUCHER_CODE)).thenReturn(Optional.of(validVoucher));
        when(redemptionRepository.countByVoucherId(validVoucher.getId())).thenReturn(5L);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> redemptionService.redeemVoucher(TEST_VOUCHER_CODE)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Voucher redemption limit reached", ex.getReason());
        verify(redemptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get redemptions for valid voucher")
    void getRedemptionsForVoucherShouldReturnRedemptions() {
        Redemption redemption = new Redemption(NOW, validVoucher);
        List<Redemption> expectedRedemptions = List.of(redemption);

        when(voucherRepository.findByCode(TEST_VOUCHER_CODE)).thenReturn(Optional.of(validVoucher));
        when(redemptionRepository.findByVoucher(validVoucher)).thenReturn(expectedRedemptions);

        List<Redemption> result = redemptionService.getRedemptionsForVoucher(TEST_VOUCHER_CODE);

        assertEquals(1, result.size());
        assertEquals(redemption, result.get(0));
        verify(redemptionRepository).findByVoucher(validVoucher);
    }

    @Test
    @DisplayName("Should throw when getting redemptions for non-existent voucher")
    void getRedemptionsForVoucherShouldThrowWhenVoucherNotFound() {
        when(voucherRepository.findByCode("INVALID_CODE")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> redemptionService.getRedemptionsForVoucher("INVALID_CODE")
        );
        assertEquals("Voucher not found", ex.getMessage());
        verify(redemptionRepository, never()).findByVoucher(any());
    }

    @Test
    @DisplayName("Should allow unlimited redemptions when no limit set")
    void redeemVoucherShouldAllowUnlimitedRedemptionsWhenNoLimit() {
        validVoucher.setRedemptionLimit(null);
        when(voucherRepository.findByCode(TEST_VOUCHER_CODE)).thenReturn(Optional.of(validVoucher));
        when(redemptionRepository.countByVoucherId(validVoucher.getId())).thenReturn(100L); // High number

        Redemption expectedRedemption = new Redemption(NOW, validVoucher);
        when(redemptionRepository.save(any(Redemption.class))).thenReturn(expectedRedemption);

        Redemption result = redemptionService.redeemVoucher(TEST_VOUCHER_CODE);

        assertNotNull(result);
        verify(redemptionRepository).save(any(Redemption.class));
    }

    @Test
    @DisplayName("Should allow redemption on last valid day")
    void redeemVoucherShouldAllowRedemptionOnLastValidDay() {
        validVoucher.setValidTo(NOW.plusSeconds(10));

        when(voucherRepository.findByCode(TEST_VOUCHER_CODE)).thenReturn(Optional.of(validVoucher));
        when(redemptionRepository.countByVoucherId(validVoucher.getId())).thenReturn(0L);

        Redemption expectedRedemption = new Redemption(NOW, validVoucher);
        when(redemptionRepository.save(any(Redemption.class))).thenReturn(expectedRedemption);

        Redemption result = redemptionService.redeemVoucher(TEST_VOUCHER_CODE);

        assertNotNull(result);
        verify(redemptionRepository).save(any(Redemption.class));
    }
}