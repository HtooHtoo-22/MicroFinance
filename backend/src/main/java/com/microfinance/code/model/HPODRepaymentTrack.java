package com.microfinance.code.model;

import com.microfinance.code.status.RepaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "hp_od_repay_track")  // Change the table name to match HP OD
public class HPODRepaymentTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hp_repay_schedule_id")  // Reference HP Schedule
    private HPSchedule hpRepaymentSchedule;

    @Column(name = "paid_interest_od_amount", precision = 19, scale = 2)
    private BigDecimal paidInterestODAmount;  // Track Interest OD Repayment

    @Column(name = "paid_principal_od_amount", precision = 19, scale = 2)
    private BigDecimal paidPrincipalODAmount;  // Track Principal OD Repayment

    @Column(name = "date")
    private LocalDateTime date;  // Repayment Date

    @Enumerated(EnumType.STRING)
    @Column(name="repayment_status")
    private RepaymentStatus repayStatus;
}

