package com.microfinance.code.model;

import com.microfinance.code.status.RepaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "sme_repayment_schedule")
public class SMERepaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(name = "term_number", nullable = false)
    private int termNumber;

    @Column(name = "principal", length = 45, nullable = false)
    private BigDecimal principal;

    @Column(name = "interest_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal interestAmount;

    @Column(name = "interest_OD_amount", precision = 12, scale = 2, nullable = true)
    private BigDecimal interestODAmount;

    @Column(name = "total_repaid_amount")
    private BigDecimal totalRepaidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 45, nullable = false)
    private RepaymentStatus status;

    @Column(name = "grace_period_end_date")  // Renamed for clarity
    private LocalDate gracePeriodEndDate;

    @Column(name = "fully_paid_date")
    @Temporal(TemporalType.DATE)
    private LocalDate fullyPaidDate;

    @ManyToOne
    @JoinColumn(name = "sme_loan_id",nullable = false)
    private SMELoan smeLoan;

    @PrePersist
    public void onCreate(){
        if (status == null){
            status = RepaymentStatus.NOT_DUE_YET;
        }
    }
}
