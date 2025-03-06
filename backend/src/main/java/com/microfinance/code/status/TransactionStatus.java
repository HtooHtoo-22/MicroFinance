package com.microfinance.code.status;

public enum TransactionStatus {

    NOT_USED("Not Used"),
    ALL_USED("All Used"),
    PARTIALLY_USED("Partially Used");
    private final String displayName;
    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
