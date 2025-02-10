package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rate")
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "rate_type", nullable = false, length = 50)
    private String rateType;
    @Column(name = "value", nullable = false)
    private Double value;
    @Column(name = "status")
    private boolean status;

}
