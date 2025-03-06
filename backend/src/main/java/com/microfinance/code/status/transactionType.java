package com.microfinance.code.status;

public enum transactionType {
    DR("DR"),
    CR("CR");

    private final String abbreviation;

    transactionType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static transactionType fromAbbreviation(String abbreviation) {
        for (transactionType type : values()) {
            if (type.abbreviation.equalsIgnoreCase(abbreviation)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for abbreviation: " + abbreviation);
    }
}