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

    private final SMERepaymentScheduleRepo scheduleRepo;
    private final TransactionRepository transactionRepo;
    private final SMEODRepaymentTrackRepo repaymentTrackRepo;
    private final CurrentAccountRepository accountRepo;
    private final SMELateFeeCalculationRepo lateFeeRepo;

    @Autowired
    public SMEODRepayService(SMERepaymentScheduleRepo scheduleRepo,
                             TransactionRepository transactionRepo,
                             SMEODRepaymentTrackRepo repaymentTrackRepo,
                             CurrentAccountRepository accountRepo,
                             SMELateFeeCalculationRepo lateFeeRepo) {
        this.scheduleRepo = scheduleRepo;
        this.transactionRepo = transactionRepo;
        this.repaymentTrackRepo = repaymentTrackRepo;
        this.accountRepo = accountRepo;
        this.lateFeeRepo = lateFeeRepo;
    }

    @Transactional
//    @Scheduled(cron = "0 * 9-23 * * *") // Runs every hour from 9 AM to 5 PM
    public void processODRepayment() {
        System.out.println("Processing OD Repayment");

        List<SMERepaymentSchedule> overdueSchedules = scheduleRepo.findByStatusInAndLateFeeStatus(
                List.of(RepaymentStatus.PARTIAL_OVERDUE, RepaymentStatus.FULL_OVERDUE), false);
        overdueSchedules.forEach(this::processRepaymentForSchedule);
    }

    private void processRepaymentForSchedule(SMERepaymentSchedule schedule) {
        CurrentAccount account = schedule.getSmeLoan().getCurrentAccount();
        BigDecimal availableFunds = calculateAvailableFunds(account);

        if (availableFunds.compareTo(BigDecimal.ZERO) <= 0) {
            applyLateFee(schedule);
            return;
        }

        BigDecimal amountToRepay = availableFunds.min(schedule.getInterestODAmount());
        updateRepaymentStatus(schedule, amountToRepay);
        updateAccountBalance(account, amountToRepay);
        logRepayment(schedule, amountToRepay);

        if (schedule.getInterestODAmount().compareTo(BigDecimal.ZERO) > 0) {
            applyLateFee(schedule);
        }
    }

    private BigDecimal calculateAvailableFunds(CurrentAccount account) {
        return transactionRepo.findByCurrentAccountIdAndDate(account, LocalDate.now())
                .stream()
                .map(transaction -> transaction.getType() == transactionType.CR
                        ? transaction.getAmount()
                        : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateRepaymentStatus(SMERepaymentSchedule schedule, BigDecimal amountToRepay) {
        BigDecimal remainingAmount = schedule.getInterestODAmount().subtract(amountToRepay);
        schedule.setInterestODAmount(remainingAmount);
        schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(amountToRepay));

        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            schedule.setStatus(RepaymentStatus.PAID);
            schedule.setFullyPaidDate(LocalDate.now());
        } else {
            schedule.setStatus(RepaymentStatus.PARTIAL_OVERDUE);
        }

        scheduleRepo.save(schedule);
    }

    private void updateAccountBalance(CurrentAccount account, BigDecimal amountToRepay) {
        account.setTotalBalence(account.getTotalBalence() - amountToRepay.doubleValue());
        accountRepo.save(account);
    }

    private void logRepayment(SMERepaymentSchedule schedule, BigDecimal amountToRepay) {
        SMEODRepaymentTrack track = new SMEODRepaymentTrack();
        track.setSmeRepaymentSchedule(schedule);
        track.setPaid_od_amount(amountToRepay);
        track.setDate(LocalDateTime.now());
        repaymentTrackRepo.save(track);
    }

    private void applyLateFee(SMERepaymentSchedule schedule) {
        System.out.println("Applying Late Fee");
        schedule.setLateFeeStatus(true);
        scheduleRepo.save(schedule);

        SMELateFeeCalculation lateFee = new SMELateFeeCalculation();
        lateFee.setSmeRepaymentSchedule(schedule);
        lateFee.setLateDays(1);
        lateFee.setLateFees(schedule.getInterestODAmount().multiply(BigDecimal.valueOf(0.001)));
        lateFeeRepo.save(lateFee);
    }
}
