package com.microfinance.code.status;

public enum DealerStatus {
    ACTIVE("ACTIVE"),
    STOP("STOP"); // Fix: Make it uppercase to match `valueOf()`

    private final String displayName;

    DealerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Convert from a String safely (case-insensitive)
    public static DealerStatus fromString(String status) {
        for (DealerStatus ds : DealerStatus.values()) {
            if (ds.displayName.equalsIgnoreCase(status)) {
                return ds;
            }
        }
        throw new IllegalArgumentException("Invalid DealerStatus: " + status);
    }
}
