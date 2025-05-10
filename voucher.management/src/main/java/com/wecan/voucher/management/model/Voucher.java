package com.wecan.voucher.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Voucher code is required")
    @Size(min = 3, max = 20, message = "Code must be between 3 and 20 characters")
    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @NotNull(message = "Voucher type is required (SINGLE, MULTIPLE, LIMITED)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherType type = VoucherType.SINGLE;

    @PositiveOrZero(message = "Redemption limit must be positive or zero")
    private Integer redemptionLimit; // Only for LIMITED type

    @NotNull(message = "Valid-from date is required")
    @Column(nullable = false)
    private LocalDate validFrom = LocalDate.now();

    @FutureOrPresent(message = "Valid-to date must be in the future")
    private LocalDate validTo;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    @Column(nullable = false)
    private Integer discountValue;

    @NotNull(message = "Discount type is required (FIXED or PERCENTAGE)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType = DiscountType.FIXED;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Redemption> redemptions;

    public enum VoucherType {
        SINGLE, MULTIPLE, LIMITED
    }

    public enum DiscountType {
        FIXED, PERCENTAGE
    }

    public Voucher() {}

    public Voucher(String code, Integer discountValue) {
        this.code = code;
        this.discountValue = discountValue;
        this.validFrom = LocalDate.now();
    }

    public Voucher(String code, VoucherType type, Integer redemptionLimit,
                   LocalDate validFrom, LocalDate validTo, Integer discountValue,
                   DiscountType discountType) {
        this.code = code;
        this.type = type;
        this.redemptionLimit = redemptionLimit;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.discountValue = discountValue;
        this.discountType = discountType;
    }

    // GETTERS
    public String getCode() {
        return code;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public Integer getDiscountValue() {
        return discountValue;
    }

    public Long getId() {
        return id;
    }

    public Integer getRedemptionLimit() {
        return redemptionLimit;
    }

    public List<Redemption> getRedemptions() {
        return redemptions;
    }

    public VoucherType getType() {
        return type;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }
    // --------------------------------------------------------------------

    // SETTERS
    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public void setDiscountValue(Integer discountValue) {
        this.discountValue = discountValue;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRedemptionLimit(Integer redemptionLimit) {
        this.redemptionLimit = redemptionLimit;
    }

    public void setRedemptions(List<Redemption> redemptions) {
        this.redemptions = redemptions;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }
    // --------------------------------------------------------------------


    @Override
    public String toString() {
        return "Voucher{" +
                "code='" + code + '\'' +
                ", id=" + id +
                ", type=" + type +
                ", redemptionLimit=" + redemptionLimit +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", discountValue=" + discountValue +
                ", discountType=" + discountType +
                ", redemptions=" + redemptions +
                '}';
    }
}
