package com.microfinance.code.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microfinance.code.status.LoanStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HPLoanDTO {
    private Integer id;
    private String loanId;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private int gracePeriod;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String registeredDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String approvedDate;

    private LoanStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    private int duration;
    private Integer entryUserId;
    private Integer approvedUserId;
    private Integer currentAccountId;
    private Integer productId;
    private BigDecimal downPaymentRate;
    private BigDecimal dealerCommissionRate;
}
