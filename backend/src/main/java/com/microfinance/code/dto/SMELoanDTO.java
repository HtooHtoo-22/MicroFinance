package com.microfinance.code.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<Integer> collateralIds;
    private LocalDate expiredDate;
    private int duration;
    private BigDecimal principal;
    private int entryUserId;
    private String entryUserGenerateId;
    private String entryUserName;
    private Integer approvedUserId;
    private String approvedUserName;
    private Integer currentAccountId;
    private String currentAccountaccId;
    private String borrowerName;
    private Integer cifId;
    private String cifIdNumber;
    private List<CollateralDTO> usedCollaterals;
    private String loanStatus;
}