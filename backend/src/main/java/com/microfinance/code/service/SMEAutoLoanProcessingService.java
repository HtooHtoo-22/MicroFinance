package com.microfinance.code.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class SMEAutoLoanProcessingService {

    @Scheduled(cron = "5 * * * * *") // Runs every minute at 5 seconds past the minutue
    //@Scheduled(cron = "5 0 23 * * *") // Runs at exactly 11:00:05 PM every day
    public void checkLateFee() {
        System.out.println("Checking transactions for late fee calculation...");

        // Step 1: Detect new transactions
        processNewTransactions();

        // Step 2: If late fee applies, update the database
        applyLateFee();
    }

    private void processNewTransactions() {
        System.out.println("Checking transactions...");
    }

    private void applyLateFee() {
        System.out.println("Applying late fees...");
    }


    @Scheduled(cron = "0 * 9-17 * * *") // Runs every minute from 9 AM to 5 PM
    //@Scheduled(cron = "0 5 23 * * *") // Runs at exactly 11:05:00 PM every day
    public void checkLoanRepayments() {
        System.out.println("Running Loan Repayment Check: " + LocalTime.now());

        // Step 1: Check if today’s date is in SME_loan repayment schedule
        checkDueRepayments();

        // Step 2: Check borrower’s current account balance
        processPayments();

        // Step 3: If insufficient funds, mark as Overdue (OD)
        markOverdueLoans();
    }

    private void checkDueRepayments() {
        // Query SME_repayment_schedule WHERE due_date = TODAY
        System.out.println("Checking for due repayments today...");
    }

    private void processPayments() {
        // If borrower has enough balance, auto-deduct payment
        System.out.println("Processing payments for borrowers...");
    }

    private void markOverdueLoans() {
        // If balance is insufficient, add entry in OD_tracking
        System.out.println("Marking overdue loans...");
    }
}
