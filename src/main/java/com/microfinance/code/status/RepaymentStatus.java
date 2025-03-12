package com.microfinance.code.status;

public enum RepaymentStatus {
    NOT_DUE_YET("Not Due Yet"),      // Due date has not arrived yet
    IN_GRACE_PERIOD("In Grace Period"),  // Due date passed, but still within the grace period
    FULL_OVERDUE("Full Overdue"),          // Grace period ended, payment is now overdue
    PARTIAL_OVERDUE("Partial Overdue"),
    PAID("Paid"),             // Fully paid
    DEFAULTED("Defaulted"),
    CANCELED("Canceled"),
    INTEREST_PAID_PRINCIPAL_OD("Interest Paid, Principal OD"),
    INTEREST_OD_PRINCIPAL_OD("Neither Interest nor Principal Paid"),
    ALL_PAID("All Paid"); // Interest and principal fully paid off without OD

    private final String displayName;

    RepaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
