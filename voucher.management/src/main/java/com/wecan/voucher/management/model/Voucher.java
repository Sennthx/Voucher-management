package com.wecan.voucher.management.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherType type = VoucherType.SINGLE; // SINGLE, MULTIPLE, LIMITED

    private Integer redemptionLimit; // Only for LIMITED type

    @Column(nullable = false)
    private Instant validFrom = Instant.now();

    private Instant validTo;

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType = DiscountType.FIXED; // FIXED or PERCENTAGE

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Redemption> redemptions;

    public enum VoucherType {
        SINGLE, MULTIPLE, LIMITED
    }

    public enum DiscountType {
        FIXED, PERCENTAGE
    }

    public Voucher() {}

    public Voucher(String code, BigDecimal discountValue) {
        this.code = code;
        this.discountValue = discountValue;
        this.validFrom = Instant.now();
    }

    public Voucher(String code, VoucherType type, Integer redemptionLimit,
                   Instant validFrom, Instant validTo, BigDecimal discountValue,
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

    public BigDecimal getDiscountValue() {
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

    public Instant getValidFrom() {
        return validFrom;
    }

    public Instant getValidTo() {
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

    public void setDiscountValue(BigDecimal discountValue) {
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

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }
    // --------------------------------------------------------------------
}
