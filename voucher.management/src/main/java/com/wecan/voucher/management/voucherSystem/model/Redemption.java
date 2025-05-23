package com.wecan.voucher.management.voucherSystem.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "redemptions")
public class Redemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @Column(nullable = false)
    private Instant redeemedAt = Instant.now();

    public Redemption() {}

    public Redemption(Voucher voucher) {
        this.voucher = voucher;
    }

    public Redemption(Instant redeemedAt, Voucher voucher) {
        this.redeemedAt = redeemedAt;
        this.voucher = voucher;
    }

    // GETTERS
    public Long getId() {
        return id;
    }

    public Instant getRedeemedAt() {
        return redeemedAt;
    }

    public Voucher getVoucher() {
        return voucher;
    }
    // --------------------------------------------------------------------

    // SETTERS
    public void setId(Long id) {
        this.id = id;
    }

    public void setRedeemedAt(Instant redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
    // --------------------------------------------------------------------


    @Override
    public String toString() {
        return "Redemption{" +
                "id=" + id +
                ", voucher=" + voucher +
                ", redeemedAt=" + redeemedAt +
                '}';
    }
}