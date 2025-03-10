package com.microfinance.code.etc.generator;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HPLoanIDGenerator {
    private static final String PREFIX = "HP";

    public static String generateLoanId() {
        String dateCode = new SimpleDateFormat("yyMMdd").format(new Date());
        int nextNumber = getNextLoanNumber(dateCode);
        String loanId = String.format("%s-%s-%04d", PREFIX, dateCode, nextNumber);
        return loanId;
    }

    private static int getNextLoanNumber(String dateCode) {
        int nextNumber = 1;
        String lastLoanId = getLastLoanId(dateCode);

        if (lastLoanId != null) {
            // Extract the last 4-digit sequential number
            String[] parts = lastLoanId.split("-");
            nextNumber = Integer.parseInt(parts[2]) + 1;
        }

        return nextNumber;
    }

    private static String getLastLoanId(String dateCode) {
        String lastLoanId = null;
        String query = "SELECT loan_id FROM hp_loan WHERE loan_id LIKE ? ORDER BY loan_id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/microfinances", "root", "myanmar122021");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, PREFIX + "-" + dateCode + "-%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                lastLoanId = rs.getString("loan_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lastLoanId;
    }
}
