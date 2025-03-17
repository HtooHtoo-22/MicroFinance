package com.microfinance.code.model;

import com.microfinance.code.status.DEALER;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "current_account_id", nullable = false)
    private CurrentAccount currentAccount;

    @Column(name = "company_value", nullable = false)
    private Double companyValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusfordelar", nullable = false, length = 10)
    private DEALER statusforDelar;

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    @PrePersist
    protected void onCreate() {
        if (statusforDelar == null) {
            statusforDelar = DEALER.PENDING;
        }
        if (registerDate == null) {
            registerDate = LocalDate.now();
        }
    }
}
