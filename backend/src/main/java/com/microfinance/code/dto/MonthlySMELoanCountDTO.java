package com.microfinance.code.dto;

public class MonthlySMELoanCountDTO {
    private String month;
    private Long approvedLoanCount;

    public MonthlySMELoanCountDTO(String month, Long approvedLoanCount) {
        this.month = month;
        this.approvedLoanCount = approvedLoanCount;
    }

    public MonthlySMELoanCountDTO() {}

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Long getApprovedLoanCount() {
        return approvedLoanCount;
    }

    public void setApprovedLoanCount(Long approvedLoanCount) {
        this.approvedLoanCount = approvedLoanCount;
    }
}