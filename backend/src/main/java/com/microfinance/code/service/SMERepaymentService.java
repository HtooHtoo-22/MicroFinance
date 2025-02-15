package com.microfinance.code.service;

import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.Holiday;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.HolidayRepository;
import com.microfinance.code.repository.SMERepaymentScheduleRepo;
import com.microfinance.code.status.RepaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SMERepaymentService {
    @Autowired
    private HolidayRepository holidayRepository; // Repository for Holiday table

    @Autowired
    private SMERepaymentScheduleRepo repaymentScheduleRepository; // Repository for SMERepaymentSchedule table

    @Autowired
    private CurrentAccountRepository currentAccountRepository; // Repository for CurrentAccount table

    @Transactional
    @Scheduled(cron = "0 * 9-17 * * *")
    public void processRepayments() {
        LocalDate today = LocalDate.now();

        // Check if today is a holiday
//        boolean isHoliday = isHoliday(today);
//        if (isHoliday) {
//            System.out.println("Today is a holiday. Skipping repayments.");
//            return;
//        }
        System.out.println("Run");
        // Proceed if it's not a holiday
        processScheduledRepayments(today);
    }
    private boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date); // Assuming holidayRepo has a method to check for holidays
    }
    @Transactional
    public void processScheduledRepayments(LocalDate today) {
        List<SMERepaymentSchedule> schedules = repaymentScheduleRepository.findByDueDate(today);

        if (schedules.isEmpty()) {
            System.out.println("No repayments due today.");
            return;
        }

        for (SMERepaymentSchedule schedule : schedules) {
            processRepayment(schedule);
        }
    }
    private void processRepayment(SMERepaymentSchedule schedule) {
        // Get the current account and total balance as BigDecimal
        CurrentAccount currentAccount = schedule.getSmeLoan().getCurrentAccount();
        BigDecimal totalBalance = BigDecimal.valueOf(currentAccount.getTotalBalence()); // Convert to BigDecimal
        BigDecimal dueAmount = schedule.getInterestAmount(); // Assume interestAmount is already BigDecimal

        if (totalBalance.compareTo(dueAmount) >= 0) {
            // Enough balance to repay
            currentAccount.setTotalBalence(totalBalance.subtract(dueAmount).doubleValue()); // Convert back to double if needed
            schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(dueAmount));
            schedule.setStatus(RepaymentStatus.PAID); // Mark as fully paid
            schedule.setFullyPaidDate(LocalDate.now());
        } else if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
            // Not enough balance, pay partially and mark the rest as interest OD
            currentAccount.setTotalBalence(0.0); // Deduct all balance
            schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(totalBalance));
            schedule.setInterestODAmount(schedule.getInterestODAmount().add(dueAmount.subtract(totalBalance))); // Remaining amount as OD interest
            schedule.setStatus(RepaymentStatus.PARTIAL_OVERDUE); // Mark as partially paid
        } else {
            // Current account is 0, mark as Full Overdue
            schedule.setInterestODAmount(schedule.getInterestODAmount().add(dueAmount)); // All remaining amount is OD interest
            schedule.setStatus(RepaymentStatus.FULL_OVERDUE); // Mark as fully overdue
        }

        // Save updated data to the database
        currentAccountRepository.save(currentAccount);
        repaymentScheduleRepository.save(schedule);
    }


}
