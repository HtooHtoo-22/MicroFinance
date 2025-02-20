package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table( name = "sme_late_fee_calculation")
public class SMELateFeeCalculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "late_days")
    private int lateDays;

    @Column( name = "late_fees")
    private BigDecimal lateFees;

    @OneToOne
    @JoinColumn(name = "sme_repayment_schedule_id")
    private SMERepaymentSchedule smeRepaymentSchedule;

}
