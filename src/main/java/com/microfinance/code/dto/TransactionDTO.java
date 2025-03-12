package com.microfinance.code.dto;

import com.microfinance.code.status.transactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDTO {
    private Integer id;
    private transactionType type;
    private BigDecimal amount;
    private LocalDateTime date;
    private String currentAccountId;
}
