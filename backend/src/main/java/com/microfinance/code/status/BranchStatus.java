package com.microfinance.code.status;

public enum BranchStatus {
    OPEN("Open"),
    CLOSE("Close"),
    Active("Active");
    private final String displayName;
    BranchStatus(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }

}
