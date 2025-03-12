package com.microfinance.code.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microfinance.code.status.LoanStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "hp_loan")
public class HPLoan {
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

    @Column(name = "tenor", nullable = true) // Added nullable
    private int tenor;

    @Column(name = "registered_date", nullable = false) // Added nullable
    private LocalDateTime registeredDate;

    @Column(name = "approved_date") // approveDate can be null initially
    private LocalDateTime approvedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanStatus status;

    @Column(name = "end_date", nullable = true) // Added nullable
    private LocalDate endDate;

    @Column(name = "duration", nullable = false) // Added nullable
    private int duration;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User entryUser;

    @ManyToOne
    @JoinColumn(name = "approvedUser_id")
    private User approvedUser;

    @ManyToOne
    @JoinColumn(name = "current_account_id", nullable = false)
    private CurrentAccount currentAccount;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Column(name = "down_payment_rate")
    private BigDecimal downPaymentRate;

    @Column(name = "dealer_commission_rate", nullable = false)
    private BigDecimal dealerCommissionRate;

    @PrePersist
    public void prePersist() {
        if (this.status == null) { // Only set if not already set
            this.status = LoanStatus.PENDING;
        }
        if(registeredDate == null){
            LocalDateTime now = LocalDateTime.now();
            registeredDate = now ;
        }
    }
}
