package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "total_balence", nullable = false)
    private Double totalBalence;

    @Column(name = "freeze_status", nullable = false)
    private boolean freezeStatus;

    @OneToOne
    @JoinColumn(name = "cif_id", nullable = false)
    private CIF cif;
}