package com.microfinance.code.status;

public enum DealerStatus {
    ACTIVE("Active"),
    Stop("Stop");
    private final String displayName;
    DealerStatus(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
