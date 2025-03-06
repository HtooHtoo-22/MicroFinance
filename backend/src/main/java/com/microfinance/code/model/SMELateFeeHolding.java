package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "sme_late_fee_holding")
public class SMELateFeeHolding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column( name = "hold_amount")
    private BigDecimal holdAmount;

    @OneToOne
    @JoinColumn(name = "sme_loan_id")
    private SMELoan smeLoan;
}
