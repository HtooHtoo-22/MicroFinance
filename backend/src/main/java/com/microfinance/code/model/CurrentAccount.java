package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "current_account")
public class CurrentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "max_Amount")
    private Double maxAmount;
    @Column(name = "min_Amount")
    private Double minAmount;
    @Column(name = "register_date")
    private LocalDateTime registerDate;
    @Column(name = "approve_date")
    private LocalDateTime approveDate;
    @Column(name = "loan_balence")
    private Double loanBalence;
    @Column(name = "account_Repaid")
    private Double accountRepaid;
    @OneToOne
    @JoinColumn(name = "cif_id")
    private CIF cif;
}
