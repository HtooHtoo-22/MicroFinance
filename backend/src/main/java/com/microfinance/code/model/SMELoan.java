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

    @Column(name = "loan_id", nullable = false, length = 30) // Added nullable and length
    private String loanId;

    @Column(name = "loan_amount", nullable = false) // Added nullable
    private BigDecimal loanAmount;

    @Column(name = "interest_rate", nullable = false) // Added nullable
    private BigDecimal interestRate;

    @Column(name = "grace_period", nullable = true) // Added nullable
    private int gracePeriod;

    @Column(name = "loan_purpose", nullable = false, length = 200) // Added nullable and length
    private String loanPurpose;

    @Column(name = "registered_date", nullable = false) // Added nullable
    private LocalDateTime registeredDate;

    @Column(name = "approved_date") // approveDate can be null initially
    private LocalDateTime approvedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanStatus status;

    @Column(name = "document_fee", nullable = false) // Added nullable
    private BigDecimal documentFee;

    @Column(name = "service_charge", nullable = false) // Added nullable
    private BigDecimal serviceCharge;

    @Column(name = "expired_date", nullable = true) // Added nullable
    private LocalDateTime expiredDate;

    @Column(name = "duration", nullable = false) // Added nullable
    private int duration;

    @Column(name = "principal", nullable = false) // Added nullable
    private BigDecimal principal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User entryUser;

    @ManyToOne
    @JoinColumn(name = "approvedUser_id", nullable = false)
    private User approvedUser;

    @ManyToOne
    @JoinColumn(name = "current_account_id", nullable = false)
    private CurrentAccount currentAccount;

    @PrePersist
    public void prePersist() {
        if (this.status == null) { // Only set if not already set
            this.status = LoanStatus.PENDING;
        }
        if(registeredDate == null){
            LocalDateTime now = LocalDateTime.now();
            registeredDate = now ;
        }
        if(principal == null){
            principal = loanAmount;
        }
    }
}