package com.microfinance.code.service;

import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.Holiday;
import com.microfinance.code.model.SMELateFeeCalculation;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.HolidayRepository;
import com.microfinance.code.repository.SMELateFeeCalculationRepo;
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
    @Autowired
    private  SMERepaymentScheduleRepo scheduleRepo;
    @Autowired
    private  SMELateFeeCalculationRepo lateFeeRepo;

    @Transactional
    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE)
    public void processRepayments() {
        LocalDate today = LocalDate.now();

        // Check if today is a holiday
//        boolean isHoliday = isHoliday(today);
//        if (isHoliday) {
//            System.out.println("Today is a holiday. Skipping repayments.");
//            return;
//        }
        System.out.println("___________________________Auto Pay__________________________________________");
        // Proceed if it's not a holiday
        processScheduledRepayments(today);
        System.out.println("______________________________________________________________________________");
    }
    private boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date); // Assuming holidayRepo has a method to check for holidays
    }
    @Transactional
    public void processScheduledRepayments(LocalDate today) {
        List<SMERepaymentSchedule> schedules = repaymentScheduleRepository.findByDueDateOrGracePeriodEndDateAndStatusIn(today,today,List.of(RepaymentStatus.NOT_DUE_YET,RepaymentStatus.IN_GRACE_PERIOD));

        if (schedules.isEmpty()) {
            System.out.println("No repayments due today.");
            return;
        }

        for (SMERepaymentSchedule schedule : schedules) {
            processRepayment(schedule);
        }
    }

    private void processRepayment(SMERepaymentSchedule schedule) {

        List<SMELateFeeCalculation> calculations = lateFeeRepo.findBySmeLoanId(schedule.getSmeLoan().getId());
        int maxLateDays = calculations.stream()
                .mapToInt(SMELateFeeCalculation::getLateDays)
                .max()
                .orElse(0);

        // Get the current account and total balance as BigDecimal
        CurrentAccount currentAccount = schedule.getSmeLoan().getCurrentAccount();
        BigDecimal totalBalance = BigDecimal.valueOf(currentAccount.getTotalBalence()); // Convert to BigDecimal
        BigDecimal dueAmount = schedule.getInterestAmount(); // Assume interestAmount is already BigDecimal
        LocalDate today = LocalDate.now();

        // Ensure the minimum balance is preserved
        BigDecimal availableBalance = totalBalance.subtract(BigDecimal.valueOf(currentAccount.getMinAmount()));

        // Check if today is still within the grace period
        if (schedule.getGracePeriodEndDate() != null && today.isBefore(schedule.getGracePeriodEndDate())) {
            schedule.setStatus(RepaymentStatus.IN_GRACE_PERIOD); // Mark as within grace period
        } else if (availableBalance.compareTo(dueAmount) >= 0) {
            // Enough balance to repay without touching the minimum balance
            currentAccount.setTotalBalence(totalBalance.subtract(dueAmount).doubleValue()); // Convert back to double if needed
            schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(dueAmount));
            schedule.setStatus(RepaymentStatus.PAID); // Mark as fully paid
            schedule.setFullyPaidDate(today);
            schedule.setInterestAmount(new BigDecimal(0.0));
        } else if (availableBalance.compareTo(BigDecimal.ZERO) > 0) {
            // Not enough balance, but partial payment can be made (without touching the minimum)
            currentAccount.setTotalBalence(currentAccount.getMinAmount()); // Keep the minimum balance intact
            schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(availableBalance));
            schedule.setInterestODAmount(schedule.getInterestODAmount().add(dueAmount.subtract(availableBalance))); // Remaining amount as OD interest
            schedule.setStatus(RepaymentStatus.PARTIAL_OVERDUE);
            if(schedule.getGracePeriodEndDate()!=null && maxLateDays<91){
                applyLateFee(schedule);
            }
            schedule.setInterestAmount(new BigDecimal(0.0));
        } else {
            // No available balance, full overdue
            schedule.setInterestODAmount(schedule.getInterestODAmount().add(dueAmount)); // All remaining amount is OD interest
            schedule.setStatus(RepaymentStatus.FULL_OVERDUE);
            if(schedule.getGracePeriodEndDate()!=null && maxLateDays<91){
                applyLateFee(schedule);
            }
            schedule.setInterestAmount(new BigDecimal(0.0));
        }

        // Save updated data to the database
        currentAccountRepository.save(currentAccount);
        repaymentScheduleRepository.save(schedule);
    }
    private void applyLateFee(SMERepaymentSchedule schedule) {
        System.out.println("Applying Late Fee");
        schedule.setLateFeeStatus(true);
        scheduleRepo.save(schedule);

        LocalDate dueDate = schedule.getDueDate();
        LocalDate graceEndDate = schedule.getGracePeriodEndDate();
        LocalDate currentDate = LocalDate.now();

        if (currentDate.equals(graceEndDate)) { // Late days start after grace period
            long lateDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);

            SMELateFeeCalculation lateFee = new SMELateFeeCalculation();
            lateFee.setSmeRepaymentSchedule(schedule);
            lateFee.setLateDays((int) lateDays);

            // Late fee based on days overdue
            BigDecimal lateFeeAmount = schedule.getInterestODAmount()
                    .multiply(BigDecimal.valueOf(0.001)) // Late fee rate
                    .multiply(BigDecimal.valueOf(lateDays)); // Multiply by late days

            lateFee.setLateFees(lateFeeAmount);
            lateFeeRepo.save(lateFee);
        }
    }

}
