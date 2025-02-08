package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "holiday")
public class Holiday {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    @Column(name = "holiday_date")
    private Data holidayDate;
    @Column(name = "holiday_name")
    private String name;

}
