package com.microfinance.code.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SMEScheduleDTO {
    private Integer id;
    private String dueDate;
    private int totalDays;
    private int termNumber;
    private BigDecimal principal;
    private BigDecimal interestAmount;
    private BigDecimal interestODAmount;
    private BigDecimal totalRepaidAmount;
    private String status;
    private String gracePeriodEndDate;
    private String fullyPaidDate;
    private boolean lateFeeStatus;
    private Integer smeLoanId; // Reference only by ID for DTO
    private SMELoanDTO smeLoanDTO;
}
