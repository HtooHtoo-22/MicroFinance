package com.microfinance.code.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HPScheduleDTO {
    private Integer id;
    private String dueDate;
    private int totalDays;
    private int termNumber;
    private BigDecimal installment;
    private BigDecimal principal;
    private BigDecimal principalOdAmount;
    private BigDecimal interestAmount;
    private BigDecimal interestODAmount;
    private BigDecimal totalRepaidAmount;
    private String status;
    private String gracePeriodEndDate;
    private String fullyPaidDate;
    private boolean lateFeeStatus;
    private Integer hpLoanId; // Reference only by ID for DTO
    private HPLoanDTO hpLoanDTO;
}
