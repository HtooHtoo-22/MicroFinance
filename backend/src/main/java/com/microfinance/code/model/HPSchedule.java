package com.microfinance.code.model;

import com.microfinance.code.status.RepaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "hp_schedule")
public class HPSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "grace_period_end_date")  // Renamed for clarity
    private LocalDate gracePeriodEndDate;

    @Column(name = "term_number", nullable = false)
    private int termNumber;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(name = "principal", length = 45, nullable = false)
    private BigDecimal principal;

    @Column(name = "interest_amount", nullable = false)
    private BigDecimal interestAmount;

    @Column(name = "installment", precision = 12, scale = 2, nullable = false)
    private BigDecimal installment;

    @Column(name = "principal_OD_amount", precision = 12, scale = 2, nullable = true)
    private BigDecimal principalODAmount;

    @Column(name = "interest_OD_amount", precision = 12, scale = 2, nullable = true)
    private BigDecimal interestODAmount;

    @Column(name = "total_repaid_amount")
    private BigDecimal totalRepaidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 45, nullable = false)
    private RepaymentStatus status;

    @Column(name = "fully_paid_date")
    @Temporal(TemporalType.DATE)
    private LocalDate fullyPaidDate;

    @Column(name="late_fee_status")
    private boolean lateFeeStatus;

    @ManyToOne
    @JoinColumn(name = "hp_loan_id",nullable = false)
    private HPLoan hpLoan;

    @PrePersist
    public void onCreate(){
        if (status == null){
            status = RepaymentStatus.NOT_DUE_YET;
        }
        if (totalRepaidAmount == null || totalRepaidAmount.compareTo(BigDecimal.ZERO) == 0) {
            totalRepaidAmount = BigDecimal.ZERO;
        }
        if (interestODAmount == null || interestODAmount.compareTo(BigDecimal.ZERO) == 0) {
            interestODAmount = BigDecimal.ZERO;
        }
        if (principalODAmount == null || principalODAmount.compareTo(BigDecimal.ZERO) == 0) {
            principalODAmount = BigDecimal.ZERO;
        }
    }
}
