package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sme_repayment_track")
public class SMERepaymentTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sme_repay_schedule_id")
    private SMERepaymentSchedule smeRepaymentSchedule;

    @Column(name = "paid_amount", precision = 19, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "date")
    private LocalDate date;

    @Column(name="od_status")
    private boolean odStatus;
}
