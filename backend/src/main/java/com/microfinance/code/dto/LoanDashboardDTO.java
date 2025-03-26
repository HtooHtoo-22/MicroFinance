package com.microfinance.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanDashboardDTO {
    private BigDecimal netCashFlow;
    private BigDecimal totalDisbursements;
    private BigDecimal totalRepayments;
    private BigDecimal outstandingPortfolio;
    private BigDecimal delinquencyRate;

    // Breakdowns
    private BigDecimal smeDisbursements;
    private BigDecimal hpDisbursements;
    private BigDecimal smeRepayments;
    private BigDecimal hpRepayments;
    private LocalDate startDate;
    private LocalDate endDate;
}
