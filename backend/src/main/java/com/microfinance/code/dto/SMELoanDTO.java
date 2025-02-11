package com.microfinance.code.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SMELoanDTO {
    private Integer id;
    private String loanId;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private int gracePeriod;
    private String loanPurpose;
    private String registeredDate;
    private String approvedDate;
    private String status;
    private BigDecimal documentFee;
    private BigDecimal serviceCharge;
    private LocalDateTime expiredDate;
    private int duration;
    private BigDecimal principal;
    private int entryUserId;
    private String entryUserName;
    private int approvedUserId;
    private String approvedUserName;
    private int currentAccountId;
    private String currentAccountaccId;
}
