package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "product_name", nullable = false, length = 45)
    private String productName;

    @Column(name="value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Column(name="photo",nullable = false, length = 500)
    private String photo;

    @ManyToOne
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @Column(name="status", nullable = false)
    private boolean status;




}
