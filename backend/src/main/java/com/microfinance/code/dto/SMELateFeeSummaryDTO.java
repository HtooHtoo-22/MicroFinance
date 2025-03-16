package com.microfinance.code.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SMELateFeeSummaryDTO {
    private List<SMEScheduleDTO> odSchedules;
    private int lateDays;
    private BigDecimal lateFees;
    private BigDecimal outStandingAmount;
    private BigDecimal holdAmount;
    private BigDecimal lateFeeRateBf90;
    private BigDecimal lateFeeRateAf90;
}
