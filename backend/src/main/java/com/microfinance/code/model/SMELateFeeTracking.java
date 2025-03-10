package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table( name = "sme_late_fee_tracking")
public class SMELateFeeTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "late_days" , nullable = false)
    private int lateDays;

    @Column( name = "total_late_fees", nullable = false)
    private BigDecimal totalLateFees;

    @ManyToOne
    @JoinColumn(name="sme_loan_id" , nullable = false)
    private SMELoan smeLoan;

    @Column(name = "late_fee_repaid_date",nullable = false)
    private LocalDate lateFeeRepaidDate;

}
