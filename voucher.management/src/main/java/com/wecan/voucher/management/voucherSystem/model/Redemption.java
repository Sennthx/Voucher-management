package com.wecan.voucher.management.voucherSystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;

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
    private LocalDate redeemedAt = LocalDate.now();

    public Redemption() {}

    public Redemption(Voucher voucher) {
        this.voucher = voucher;
    }

    public Redemption(LocalDate redeemedAt, Voucher voucher) {
        this.redeemedAt = redeemedAt;
        this.voucher = voucher;
    }

    // GETTERS
    public Long getId() {
        return id;
    }

    public LocalDate getRedeemedAt() {
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

    public void setRedeemedAt(LocalDate redeemedAt) {
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