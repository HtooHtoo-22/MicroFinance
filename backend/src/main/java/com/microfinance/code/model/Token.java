package com.microfinance.code.model;

import com.microfinance.code.status.TokenType;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, columnDefinition = "LONGTEXT")
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.BEARER;

    private boolean expired;

    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}