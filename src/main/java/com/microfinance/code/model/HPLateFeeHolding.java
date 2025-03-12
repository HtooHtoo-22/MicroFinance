package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "hp_late_fee_holding")
public class HPLateFeeHolding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column( name = "hold_amount")
    private BigDecimal holdAmount;

    @OneToOne
    @JoinColumn(name = "hp_loan_id")
    private HPLoan hpLoan;
}
