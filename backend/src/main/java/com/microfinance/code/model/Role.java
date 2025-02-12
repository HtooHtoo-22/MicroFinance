package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role_name", length = 20, nullable = false)
    private String roleName;
    @Column(name = "role_description", length = 80, nullable = false)
    private String roleDescription;

    @Column(name = "status",nullable = false)
    private boolean status;
}