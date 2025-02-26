package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "hp_loan")
public class HPLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "loan_id", nullable = false, length = 30) // Added nullable and length
    private String loanId;

    @Column(name = "loan_amount", nullable = false) // Added nullable
    private BigDecimal loanAmount;

    @Column(name = "interest_rate", nullable = false) // Added nullable
    private BigDecimal interestRate;

    @Column(name = "grace_period", nullable = true) // Added nullable
    private int gracePeriod;
}
