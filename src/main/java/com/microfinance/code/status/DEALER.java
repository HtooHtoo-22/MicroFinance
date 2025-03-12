package com.microfinance.code.status;

public enum DEALER {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    REJECTED("REJECTED");

    private final String displayName;

    DEALER(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DEALER fromString(String status) {
        for (DEALER ds : values()) {
            if (ds.displayName.equalsIgnoreCase(status)) {
                return ds;
            }
        }
        throw new IllegalArgumentException("Invalid DealerStatus: " + status);
    }
}