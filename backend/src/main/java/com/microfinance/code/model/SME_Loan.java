package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "SME_Loan")
public class SME_Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "loan_id", nullable = false, length = 30) // Added nullable and length
    private String loanId;

    @Column(name = "loan_amount", nullable = false) // Added nullable
    private BigDecimal loanAmount;

    @Column(name = "interest_rate", nullable = false) // Added nullable
    private BigDecimal interestRate;

    @Column(name = "grace_period", nullable = false) // Added nullable
    private int gracePeriod;

    @Column(name = "loan_purpose", nullable = false, length = 200) // Added nullable and length
    private String loanPurpose;

    @Column(name = "register_date", nullable = false) // Added nullable
    private LocalDateTime registerDate;

    @Column(name = "approve_date") // approveDate can be null initially
    private LocalDateTime approveDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20) // Already has nullable and length
    private LoanStatus status;

    @Column(name = "document_fee", nullable = false) // Added nullable
    private BigDecimal documentFee;

    @Column(name = "service_charge", nullable = false) // Added nullable
    private BigDecimal serviceCharge;

    @Column(name = "expired_date", nullable = false) // Added nullable
    private LocalDateTime expiredDate;

    @Column(name = "duration", nullable = false) // Added nullable
    private int duration;

    @Column(name = "principal", nullable = false) // Added nullable
    private BigDecimal principal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User Entryuser;

    @ManyToOne
    @JoinColumn(name = "approveUser_id", nullable = false)
    private User approveUser;

    @ManyToOne
    @JoinColumn(name = "current_account_id", nullable = false)
    private CurrentAccount currentAccount;
}