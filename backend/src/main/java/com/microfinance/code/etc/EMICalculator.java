package com.microfinance.code.etc;

public class EMICalculator {
    public static double calculateEMI(double principal, double annualRate, int months,int tenor) {
        double result = (principal+(principal+annualRate/100*tenor))/36;
        double totalInterestAmount = principal * annualRate/100 * tenor;
        System.out.println("Total Interest Amount "+totalInterestAmount);
        double allAmount = principal + totalInterestAmount;
        return allAmount/months;
    }

    public static void main(String[] args) {
        double loanAmount = 220_000_000.00; // Principal
        double annualInterestRate = 13; // Annual interest rate in percentage
        int loanTermMonths = 36; // Loan tenure in months
        int tenor = 3;
        double emi = calculateEMI(loanAmount, annualInterestRate, loanTermMonths,tenor);
        System.out.printf("EMI Payment: %.2f%n", emi);
    }
}
