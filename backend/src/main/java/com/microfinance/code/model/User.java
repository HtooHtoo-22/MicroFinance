package com.microfinance.code.model;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails{

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
    @Column(name = "create_date", nullable = false)
    private LocalDateTime CreateDate;
    @Column(name = "status")
    private boolean active;
    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;
    @PrePersist
    protected void onCreate() {
        if (CreateDate == null) {
            LocalDateTime now = LocalDateTime.now();
            CreateDate = now;
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRoleName()));
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

}