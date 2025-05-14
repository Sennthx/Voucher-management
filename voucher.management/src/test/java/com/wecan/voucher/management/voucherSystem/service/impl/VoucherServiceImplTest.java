package com.wecan.voucher.management.voucherSystem.service.impl;

import com.wecan.voucher.management.exception.DuplicateResourceException;
import com.wecan.voucher.management.voucherSystem.model.Voucher;
import com.wecan.voucher.management.voucherSystem.model.Voucher.DiscountType;
import com.wecan.voucher.management.voucherSystem.model.Voucher.VoucherType;
import com.wecan.voucher.management.voucherSystem.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTest {

    @Mock
    private VoucherRepository voucherRepository;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    private Voucher baseVoucher;

    @BeforeEach
    void setUp() {
        // Base valid voucher that tests can modify
        baseVoucher = new Voucher();
        baseVoucher.setCode("TEST123");
        baseVoucher.setType(VoucherType.SINGLE);
        baseVoucher.setDiscountType(DiscountType.PERCENTAGE);
        baseVoucher.setDiscountValue(50);
    }

    @Test
    @DisplayName("Should save single voucher with redemption limit 1")
    void createVoucherShouldSaveSingleVoucherWithRedemptionLimit1() {
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());
        when(voucherRepository.save(baseVoucher)).thenReturn(baseVoucher);

        Voucher result = voucherService.createVoucher(baseVoucher);

        assertEquals(1, result.getRedemptionLimit());
        verify(voucherRepository).save(baseVoucher);
    }

    @Test
    @DisplayName("Should throw when voucher code already exists")
    void createVoucherShouldThrowIfCodeAlreadyExists() {
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.of(new Voucher()));

        assertThrows(DuplicateResourceException.class,
                () -> voucherService.createVoucher(baseVoucher));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when multiple voucher has invalid redemption limit")
    void createVoucherShouldThrowIfRedemptionLimitInvalidForMultiple() {
        baseVoucher.setType(VoucherType.MULTIPLE);
        baseVoucher.setRedemptionLimit(1);
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.createVoucher(baseVoucher)
        );
        assertTrue(ex.getMessage().contains("Redemption limit must be greater than 1"));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when limited voucher has missing dates")
    void createVoucherShouldThrowIfDatesMissingForLimited() {
        baseVoucher.setType(VoucherType.LIMITED);
        baseVoucher.setRedemptionLimit(5);
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.createVoucher(baseVoucher)
        );
        assertTrue(ex.getMessage().contains("Valid-from and valid-to are required"));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when validTo is before validFrom")
    void createVoucherShouldThrowIfValidToBeforeValidFrom() {
        baseVoucher.setType(VoucherType.LIMITED);
        baseVoucher.setRedemptionLimit(5);
        baseVoucher.setValidFrom(LocalDate.now());
        baseVoucher.setValidTo(LocalDate.now().minusDays(1));
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.createVoucher(baseVoucher)
        );
        assertTrue(ex.getMessage().contains("Valid-to date must be after or equal to valid-from date"));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when validTo is the past")
    void createVoucherShouldThrowIfValidToInPast() {
        baseVoucher.setType(VoucherType.LIMITED);
        baseVoucher.setRedemptionLimit(5);
        baseVoucher.setValidFrom(LocalDate.now().minusDays(6));
        baseVoucher.setValidTo(LocalDate.now().minusDays(5));
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.createVoucher(baseVoucher)
        );
        assertTrue(ex.getMessage().contains("Valid-to date cannot be in the past"));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when percentage discount exceeds 100%")
    void createVoucherShouldThrowIfPercentageDiscountOver100() {
        baseVoucher.setDiscountValue(150);
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.createVoucher(baseVoucher)
        );
        assertTrue(ex.getMessage().contains("Percentage discount cannot be greater than 100%"));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when fixed amount discount is negative")
    void createVoucherShouldThrowIfFixedAmountNegative() {
        baseVoucher.setDiscountType(DiscountType.FIXED);
        baseVoucher.setDiscountValue(-10);
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.createVoucher(baseVoucher)
        );
        assertTrue(ex.getMessage().contains("Fixed amount discount cannot be negative"));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return voucher when found by code")
    void getByCodeShouldReturnVoucherIfFound() {
        Voucher expected = new Voucher();
        expected.setCode("FOUND");
        when(voucherRepository.findByCode("FOUND")).thenReturn(Optional.of(expected));

        Optional<Voucher> result = voucherService.getByCode("FOUND");

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verify(voucherRepository).findByCode("FOUND");
    }

    @Test
    @DisplayName("Should return empty when voucher not found by code")
    void getByCodeShouldReturnEmptyWhenNotFound() {
        when(voucherRepository.findByCode("NOTFOUND")).thenReturn(Optional.empty());

        Optional<Voucher> result = voucherService.getByCode("NOTFOUND");

        assertTrue(result.isEmpty());
        verify(voucherRepository).findByCode("NOTFOUND");
    }

    @Test
    @DisplayName("Should retrieve all vouchers")
    void getAllVouchersShouldCallFindAll() {
        voucherService.getAllVouchers();
        verify(voucherRepository).findAll();
    }

    @Test
    @DisplayName("Should delete voucher by ID")
    void deleteVoucherShouldCallRepositoryDelete() {
        voucherService.deleteVoucher(1L);
        verify(voucherRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should successfully create valid multiple voucher")
    void createVoucherShouldCreateValidMultipleVoucher() {
        baseVoucher.setType(VoucherType.MULTIPLE);
        baseVoucher.setRedemptionLimit(10);
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());
        when(voucherRepository.save(baseVoucher)).thenReturn(baseVoucher);

        Voucher result = voucherService.createVoucher(baseVoucher);

        assertEquals(10, result.getRedemptionLimit());
        verify(voucherRepository).save(baseVoucher);
    }

    @Test
    @DisplayName("Should successfully create valid limited voucher")
    void createVoucherShouldCreateValidLimitedVoucher() {
        baseVoucher.setType(VoucherType.LIMITED);
        baseVoucher.setRedemptionLimit(5);
        baseVoucher.setValidFrom(LocalDate.now());
        baseVoucher.setValidTo(LocalDate.now().plusDays(30));
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());
        when(voucherRepository.save(baseVoucher)).thenReturn(baseVoucher);

        Voucher result = voucherService.createVoucher(baseVoucher);

        assertEquals(5, result.getRedemptionLimit());
        assertNotNull(result.getValidFrom());
        assertNotNull(result.getValidTo());
        verify(voucherRepository).save(baseVoucher);
    }

    @Test
    @DisplayName("Should throw when discount value is null")
    void createVoucherShouldThrowIfDiscountValueNull() {
        baseVoucher.setDiscountValue(null);
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.createVoucher(baseVoucher)
        );
        assertTrue(ex.getMessage().contains("Discount value cannot be null"));
        verify(voucherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when voucher type is null")
    void createVoucherShouldThrowIfTypeNull() {
        baseVoucher.setType(null);
        when(voucherRepository.findByCode("TEST123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voucherService.createVoucher(baseVoucher)
        );
        assertTrue(ex.getMessage().contains("Voucher type cannot be null"));
        verify(voucherRepository, never()).save(any());
    }
}