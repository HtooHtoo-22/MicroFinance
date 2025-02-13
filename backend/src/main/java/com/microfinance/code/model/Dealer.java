package com.microfinance.code.model;

import com.microfinance.code.status.DealerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "dealer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "business_name", nullable = false, length = 45)
    private String businessName;

    @Column(name = "address", nullable = false, length = 45)
    private String address;

    @Column(name = "phone", nullable = false, length = 45)
    private String phone;

    @Column(name = "email", nullable = false, length = 45, unique = true)
    private String email;

    @Column(name = "register_date", nullable = false)
    private LocalDate registerDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private DealerStatus status;

    @Column(name = "password", nullable = false, length = 255)
    private String password; // Store hashed password

    @ManyToOne
    @JoinColumn(name = "current_account_id", nullable = false)
    private CurrentAccount currentAccount;
}
