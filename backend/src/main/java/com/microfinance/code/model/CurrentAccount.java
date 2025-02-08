package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "current_account")
public class CurrentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_id", nullable = false, length = 30)
    private String accountId;

    @Column(name = "max_Amount", nullable = false)
    private Double maxAmount;

    @Column(name = "min_Amount", nullable = false)
    private Double minAmount;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime create_date;

    @Column(name = "total_balence", nullable = false)
    private Double totalBalence;

    @Column(name = "freeze_status", nullable = false)
    private boolean freezeStatus;

    @OneToOne
    @JoinColumn(name = "cif_id", nullable = false)
    private CIF cif;
}