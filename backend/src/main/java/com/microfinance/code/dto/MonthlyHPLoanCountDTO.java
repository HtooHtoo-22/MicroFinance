package com.microfinance.code.dto;

public class MonthlyHPLoanCountDTO {
    private String month;
    private Long approvedLoanCount;

    public MonthlyHPLoanCountDTO(String month, Long approvedLoanCount) {
        this.month = month;
        this.approvedLoanCount = approvedLoanCount;
    }

    public MonthlyHPLoanCountDTO() {}

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
