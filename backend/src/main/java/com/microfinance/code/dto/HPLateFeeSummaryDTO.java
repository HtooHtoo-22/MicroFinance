package com.microfinance.code.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HPLateFeeSummaryDTO {
    private List<HPScheduleDTO> odSchedules;
    private int lateDays;
    private BigDecimal interestLateFees;
    private BigDecimal principalLateFees;
    private BigDecimal outStandingAmount;
    private BigDecimal holdAmount;
    private BigDecimal lateFeeRateBf90;
    private BigDecimal lateFeeRateAf90;
}
