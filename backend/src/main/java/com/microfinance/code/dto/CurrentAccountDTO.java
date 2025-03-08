package com.microfinance.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CurrentAccountDTO {
    private Integer id;
    private String accountId;
    private Double maxAmount;
    private Double minAmount;
    private LocalDateTime createDate;
    private Double totalBalance;
    private boolean freezeStatus;
    private Integer cifId;
    private String userName;
}