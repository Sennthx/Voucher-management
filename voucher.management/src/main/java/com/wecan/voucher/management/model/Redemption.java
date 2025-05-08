package com.wecan.voucher.management.model;

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

    @Column(nullable = false, length = 45)
    private String redeemerIp;


    public Redemption() {}

    public Redemption(Voucher voucher) {
        this.voucher = voucher;
    }

    public Redemption(Instant redeemedAt, String redeemerIp, Voucher voucher) {
        this.redeemedAt = redeemedAt;
        this.redeemerIp = redeemerIp;
        this.voucher = voucher;
    }

    // GETTERS
    public Long getId() {
        return id;
    }

    public Instant getRedeemedAt() {
        return redeemedAt;
    }

    public String getRedeemerIp() {
        return redeemerIp;
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

    public void setRedeemerIp(String redeemerIp) {
        this.redeemerIp = redeemerIp;
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
                ", redeemerIp='" + redeemerIp + '\'' +
                '}';
    }
}