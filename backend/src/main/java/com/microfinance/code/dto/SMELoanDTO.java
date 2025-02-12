package com.microfinance.code.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SMELoanDTO {
    private Integer id;
    private String loanId; // Add loanId field back
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

    private Integer entryUserId;

    private String entryUserName;

    private Integer approvedUserId;

    private String approvedUserName;

    private Integer currentAccountId;

    private String currentAccountaccId;

}