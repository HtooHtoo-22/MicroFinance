package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor // This is required for JPA
@Table(name = "sme_loan_has_collateral")
public class SMELoanHasCollateral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "collateral_id", nullable = false) // Added nullable
    private Collateral collateral;

    @ManyToOne
    @JoinColumn(name = "sme_loan_id", nullable = false) // Added nullable
    private SMELoan smeLoan;

    @Column(name = "used_value")
    private BigDecimal usedValue;

    @PrePersist
    public void onCreate(){
        this.usedValue = new BigDecimal("22.2222");
    }

    public SMELoanHasCollateral(SMELoan smeLoan, Collateral collateral) {
        this.smeLoan = smeLoan;
        this.collateral = collateral;

    }
}
