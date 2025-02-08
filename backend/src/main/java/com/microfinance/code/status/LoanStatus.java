package com.microfinance.code.status;

public enum LoanStatus {
    APPROVE("Approve"),
    REJECT("Reject"),
    PENDING("Pending");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
