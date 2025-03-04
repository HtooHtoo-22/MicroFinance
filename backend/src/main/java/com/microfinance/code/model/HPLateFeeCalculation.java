package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "hp_late_fee_calculation")
public class HPLateFeeCalculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "late_days")
    private int lateDays;

    @Column(name = "interest_late_fee", precision = 19, scale = 2)
    private BigDecimal interestLateFee;

    @Column(name = "principal_late_fee", precision = 19, scale = 2)
    private BigDecimal principalLateFee;

    @OneToOne
    @JoinColumn(name = "hp_repayment_schedule_id")
    private HPSchedule hpRepaymentSchedule;
}
