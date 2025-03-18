package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sme_od_repay_track")
public class SMEODRepaymentTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sme_repay_schedule_id")
    private SMERepaymentSchedule smeRepaymentSchedule;

    @Column(name = "paid_od_amount", precision = 19, scale = 2)
    private BigDecimal paid_od_amount;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "od_end_status")
    private boolean odEndStatus;



}

