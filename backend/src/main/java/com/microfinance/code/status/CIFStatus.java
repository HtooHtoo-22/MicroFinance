package com.microfinance.code.status;

public enum CIFStatus {
    ACTIVE("Active"),
    DELETE("Delete");
    private final String displayName;
    CIFStatus(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
