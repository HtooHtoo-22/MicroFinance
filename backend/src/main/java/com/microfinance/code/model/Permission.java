package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "permission")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "permission_name", nullable = false, length = 50, unique = true)
    private String permissionName;

    @Column(name = "description")
    private String description;

    @Column(name = "active")
    private boolean active;
}