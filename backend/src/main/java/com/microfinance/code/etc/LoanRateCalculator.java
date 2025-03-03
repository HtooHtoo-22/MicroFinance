package com.microfinance.code.etc;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoanRateCalculator {

    public static double calculateMonthlyRate(int months, double loanAmount, double monthlyPayment) {
        double low = 0.0;
        double high = 1.0; // 100% interest rate
        double mid = 0.0;
        double epsilon = 0.0000001; // Precision

        while ((high - low) > epsilon) {
            mid = (low + high) / 2;
            double calculatedPayment = loanAmount * (mid / (1 - Math.pow(1 + mid, -months)));

            if (calculatedPayment > monthlyPayment) {
                high = mid;
            } else {
                low = mid;
            }
        }

        // Convert to percentage (1.90089% instead of decimal)
        double monthlyRate = mid * 100;

        // Round to 5 decimal places
        return BigDecimal.valueOf(monthlyRate)
                .setScale(5, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static void main(String[] args) {
        int months = 36; // 3 years
        double loanAmount = 220000000;
        double monthlyPayment = 8494444.44; // From your Excel sheet

        double monthlyRate = calculateMonthlyRate(months, loanAmount, monthlyPayment);
        System.out.println("Monthly Rate (BMF): " + monthlyRate + "%");
    }
}
