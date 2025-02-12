package com.microfinance.code.model;

import com.microfinance.code.status.LoanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sme_loan")
public class SMELoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "loan_id", nullable = false, length = 30, unique = true) // Ensure loanId is unique
    private String loanId;

    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "grace_period", nullable = true)
    private int gracePeriod;

    @Column(name = "loan_purpose", nullable = false, length = 200)
    private String loanPurpose;

    @Column(name = "registered_date", nullable = false)
    private LocalDateTime registeredDate;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanStatus status;

    @Column(name = "document_fee", nullable = false)
    private BigDecimal documentFee;

    @Column(name = "service_charge", nullable = false)
    private BigDecimal serviceCharge;

    @Column(name = "expired_date", nullable = true)
    private LocalDateTime expiredDate;

    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "principal", nullable = false)
    private BigDecimal principal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User entryUser;

    @ManyToOne
    @JoinColumn(name = "approvedUser_id", nullable = true)
    private User approvedUser;

    @ManyToOne
    @JoinColumn(name = "current_account_id", nullable = false)
    private CurrentAccount currentAccount;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = LoanStatus.PENDING;
        }
        if (this.registeredDate == null) {
            this.registeredDate = LocalDateTime.now();
        }
        if (this.principal == null) {
            this.principal = this.loanAmount;
        }
        if (this.loanId == null) {
            this.loanId = generateLoanId(); // Generate loanId automatically
        }
    }

    private String generateLoanId() {
        return "LOAN" + System.currentTimeMillis(); // Example: LOAN1696156800000
    }
}