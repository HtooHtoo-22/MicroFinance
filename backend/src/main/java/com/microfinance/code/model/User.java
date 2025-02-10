package com.microfinance.code.model;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_id", length = 30, nullable = false)
    private String userId;
    @Column(name = "name", length = 65, nullable = false)
    private String name;
    @Column(name = "email", unique = true, length = 60, nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @Column(name = "registered_date", nullable = false)
    private LocalDateTime registeredDate;

    @PrePersist
    protected void onCreate() {
        if (registeredDate == null) {
            LocalDateTime now = LocalDateTime.now();
            registeredDate = now;
        }

    }
}