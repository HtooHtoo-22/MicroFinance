package com.microfinance.code.status;

public enum transactionType {
    DR("DR"),
    CR("CR");
    private final String displayName;
    transactionType(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }

}
