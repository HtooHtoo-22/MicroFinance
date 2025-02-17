package com.microfinance.code.service;

import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.status.RepaymentStatus;
import com.microfinance.code.status.transactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SMEODRepayService {

    private final SMERepaymentScheduleRepo scheduleRepository;
    private final TransactionRepository transactionRepository;
    private final SMEODRepaymentTrackRepo odRepayTrackRepository;
    private final CurrentAccountRepository currentAccountRepository;

    @Autowired
    public SMEODRepayService(SMERepaymentScheduleRepo scheduleRepository,
                             TransactionRepository transactionRepository,
                             SMEODRepaymentTrackRepo odRepayTrackRepository,
                             CurrentAccountRepository currentAccountRepository) {
        this.scheduleRepository = scheduleRepository;
        this.transactionRepository = transactionRepository;
        this.odRepayTrackRepository = odRepayTrackRepository;
        this.currentAccountRepository = currentAccountRepository;
    }

    @Transactional
    @Scheduled(cron = "0 * 9-17 * * *") // Runs every hour from 9 AM to 5 PM
    public void processODRepayment() {
        System.out.println("OD Repayment Process Running...");

        List<SMERepaymentSchedule> overdueSchedules = scheduleRepository.findByStatusIn(
                List.of(RepaymentStatus.PARTIAL_OVERDUE, RepaymentStatus.FULL_OVERDUE));
        System.out.println(overdueSchedules);
        if (overdueSchedules.isEmpty()) return;

        for (SMERepaymentSchedule overdueSchedule : overdueSchedules) {
            processRepaymentForSchedule(overdueSchedule);
        }
    }

    private void processRepaymentForSchedule(SMERepaymentSchedule overdueSchedule) {
        System.out.println("Process Repay");
        CurrentAccount account = overdueSchedule.getSmeLoan().getCurrentAccount();
        BigDecimal availableFunds = calculateAvailableFunds(account);
        System.out.println(availableFunds);
        if (availableFunds.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal odAmount = overdueSchedule.getInterestODAmount();
        BigDecimal amountUsedForRepayment = availableFunds.min(odAmount);

        updateRepaymentStatus(overdueSchedule, amountUsedForRepayment);
        updateAccountBalance(account, amountUsedForRepayment);
        logRepayment(overdueSchedule, amountUsedForRepayment);
    }

    private BigDecimal calculateAvailableFunds(CurrentAccount account) {
        System.out.println("Caculate Availabe");
        BigDecimal availableFunds = BigDecimal.ZERO;
        List<Transaction> transactions = transactionRepository.findByCurrentAccountIdAndDate(account, LocalDate.now());


        for (Transaction transaction : transactions) {
            availableFunds = transaction.getType() == transactionType.CR
                    ? availableFunds.add(transaction.getAmount())
                    : availableFunds.subtract(transaction.getAmount());
        }
        return availableFunds;
    }

    private void updateRepaymentStatus(SMERepaymentSchedule overdueSchedule, BigDecimal amountUsedForRepayment) {
        System.out.println("Update Repay");
        BigDecimal newInterestODAmount = overdueSchedule.getInterestODAmount().subtract(amountUsedForRepayment);
        overdueSchedule.setInterestODAmount(newInterestODAmount);

        overdueSchedule.setTotalRepaidAmount(overdueSchedule.getTotalRepaidAmount().add(amountUsedForRepayment));

        if (newInterestODAmount.compareTo(BigDecimal.ZERO) == 0) {
            overdueSchedule.setStatus(RepaymentStatus.PAID);
        } else {
            overdueSchedule.setStatus(RepaymentStatus.PARTIAL_OVERDUE);
        }

        try {
            scheduleRepository.save(overdueSchedule);
        } catch (Exception e) {
            System.err.println("Error saving repayment schedule: " + e.getMessage());
        }
    }

    private void updateAccountBalance(CurrentAccount account, BigDecimal amountUsedForRepayment) {
        System.out.println("Update Account Balence");
        // Convert the BigDecimal to a double for the balance subtraction
        double repaymentAmount = amountUsedForRepayment.doubleValue();
        double updatedBalance = account.getTotalBalence() - repaymentAmount;

        // Update the account balance
        account.setTotalBalence(updatedBalance);
        try {
            currentAccountRepository.save(account);
        } catch (Exception e) {
            System.err.println("Error updating account balance: " + e.getMessage());
        }
    }


    private void logRepayment(SMERepaymentSchedule overdueSchedule, BigDecimal amountUsedForRepayment) {
        SMEODRepaymentTrack odRepaymentTrack = new SMEODRepaymentTrack();
        odRepaymentTrack.setSmeRepaymentSchedule(overdueSchedule);
        odRepaymentTrack.setPaid_od_amount(amountUsedForRepayment);
//
        odRepaymentTrack.setDate(LocalDateTime.now());

        try {
            odRepayTrackRepository.save(odRepaymentTrack);
        } catch (Exception e) {
            System.err.println("Error saving repayment track: " + e.getMessage());
        }
    }
}
