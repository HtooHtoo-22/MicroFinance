package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "collateral")
public class Collateral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "value", nullable = false) // Added nullable
    private BigDecimal value;

    @Column(name = "description", nullable = false, length = 500) // Added nullable and length
    private String description;

    @Column(name = "status", nullable = false) // Added nullable
    private boolean status;

    @Column(name = "address", nullable = false, length = 200) // Added nullable and length
    private String address;

    @Column(name = "image", nullable = false, length = 255) // Added nullable and length
    private String image;

    @ManyToOne
    @JoinColumn(name = "SME_Loan_id", nullable = false) // Added nullable
    private SME_Loan smeLoanId;

    @ManyToOne
    @JoinColumn(name = "colleteral_type", nullable = false) // Added nullable
    private CollateralType colleteralType;
}