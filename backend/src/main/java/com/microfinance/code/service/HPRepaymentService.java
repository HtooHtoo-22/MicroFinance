package com.microfinance.code.service;



import com.microfinance.code.model.HPLateFeeCalculation;
import com.microfinance.code.model.HPLoan;
import com.microfinance.code.model.HPSchedule;
import com.microfinance.code.model.SMELateFeeCalculation;
import com.microfinance.code.repository.*;


import com.microfinance.code.status.RepaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class HPRepaymentService {

    @Autowired
    private HPScheduleRepo hpScheduleRepository; // Repository for HPSchedule table

    @Autowired
    private HPLoanRepo hpLoanRepository; // Repository for HPLoan table

    @Autowired
    private HPLateFeeCalculationRepo lateFeeRepo; // Repository for Late Fee Calculation

    @Autowired
    private HolidayRepository holidayRepository;


    @Transactional
    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE)
    public void processRepayments() {
        LocalDate today = LocalDate.now();
        boolean isHoliday = isHoliday(today);
        if (isHoliday) {
            System.out.println("Today is a holiday. Skipping repayments.");
            return;
        }
        System.out.println("___________________________HP Auto Pay__________________________________________");

        // Process repayments for today
        processScheduledRepayments(today);

        System.out.println("______________________________________________________________________________");
    }
    private boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date); // Assuming holidayRepo has a method to check for holidays
    }
    @Transactional
    public void processScheduledRepayments(LocalDate today) {

        // Find all HP schedules that are due today or in the grace period
        List<HPSchedule> schedules = hpScheduleRepository.findByDueDateOrGracePeriodEndDateAndStatusIn(
                today,  List.of(RepaymentStatus.NOT_DUE_YET, RepaymentStatus.IN_GRACE_PERIOD)
        );

        if (schedules.isEmpty()) {
            System.out.println("No HP repayments due today.");
            return;
        }
        System.out.println(schedules);
        for (HPSchedule schedule : schedules) {
            processRepayment(schedule);
        }
    }

    private void processRepayment(HPSchedule schedule) {
        List<HPLateFeeCalculation> calculations = lateFeeRepo.findByHpLoanId(schedule.getHpLoan().getId());
        int maxLateDays = calculations.stream()
                .mapToInt(HPLateFeeCalculation::getLateDays)
                .max()
                .orElse(0);
        LocalDate today = LocalDate.now();
        if (schedule.getGracePeriodEndDate() != null && today.isBefore(schedule.getGracePeriodEndDate())) {
            schedule.setStatus(RepaymentStatus.IN_GRACE_PERIOD); // Mark as within grace period
            hpScheduleRepository.save(schedule);
            return;
        }
        // Get HP Loan and current account data
        // Get HP Loan and current account data
        HPLoan hpLoan = schedule.getHpLoan();
        BigDecimal totalBalance = BigDecimal.valueOf(hpLoan.getCurrentAccount().getTotalBalence());
        BigDecimal minBalance = BigDecimal.valueOf(hpLoan.getCurrentAccount().getMinAmount());
        BigDecimal dueInterest = schedule.getInterestAmount();
        BigDecimal duePrincipal = schedule.getPrincipal();


// Ensure minimum balance is preserved
        BigDecimal availableBalance = totalBalance.subtract(minBalance);
        BigDecimal totalRepaidAmount = BigDecimal.ZERO;

// Deduct interest first
        if (availableBalance.compareTo(dueInterest) >= 0) {
            availableBalance = availableBalance.subtract(dueInterest);
            totalRepaidAmount = totalRepaidAmount.add(dueInterest);
            schedule.setInterestODAmount(BigDecimal.ZERO);
            schedule.setStatus(RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD);
        } else if (availableBalance.compareTo(BigDecimal.ZERO) > 0) {
            totalRepaidAmount = totalRepaidAmount.add(availableBalance);
            schedule.setInterestODAmount(dueInterest.subtract(availableBalance));
            availableBalance = BigDecimal.ZERO;
            schedule.setStatus(RepaymentStatus.INTEREST_OD_PRINCIPAL_OD);
        } else {
            schedule.setInterestODAmount(dueInterest);
            schedule.setPrincipalODAmount(duePrincipal);
            schedule.setStatus(RepaymentStatus.INTEREST_OD_PRINCIPAL_OD);
        }

// Deduct principal if balance allows
        if (availableBalance.compareTo(duePrincipal) >= 0) {
            availableBalance = availableBalance.subtract(duePrincipal);
            totalRepaidAmount = totalRepaidAmount.add(duePrincipal);
            schedule.setPrincipalODAmount(BigDecimal.ZERO);
            schedule.setStatus(RepaymentStatus.ALL_PAID);
            schedule.setFullyPaidDate(today);
        } else if (availableBalance.compareTo(BigDecimal.ZERO) > 0) {
            totalRepaidAmount = totalRepaidAmount.add(availableBalance);
            schedule.setPrincipalODAmount(duePrincipal.subtract(availableBalance));
            availableBalance = BigDecimal.ZERO;
        } else {
            schedule.setPrincipalODAmount(duePrincipal);
        }

// Update account balance **after** processing both interest & principal
        BigDecimal newTotalBalance = minBalance.add(availableBalance);
        hpLoan.getCurrentAccount().setTotalBalence(newTotalBalance.doubleValue());

// Update schedule
        schedule.setInstallment(BigDecimal.ZERO);
        schedule.setInterestAmount(BigDecimal.ZERO);
        schedule.setPrincipal(BigDecimal.ZERO);
        schedule.setTotalRepaidAmount(totalRepaidAmount);

// Save changes
        hpLoanRepository.save(hpLoan);
        hpScheduleRepository.save(schedule);

        if(schedule.getGracePeriodEndDate()!=null && today.isEqual(schedule.getGracePeriodEndDate())
                && (schedule.getInterestODAmount()!=null || schedule.getPrincipalODAmount()!=null) && maxLateDays < 91){
            applyLateFee(schedule);
        }

    }

    private void applyLateFee(HPSchedule schedule) {
        System.out.println("Applying Late Fee");
        schedule.setLateFeeStatus(true); // Mark late fee status as true
        hpScheduleRepository.save(schedule);

        LocalDate dueDate = schedule.getDueDate();
        LocalDate graceEndDate = schedule.getGracePeriodEndDate();
        LocalDate currentDate = LocalDate.now();

        if (currentDate.equals(graceEndDate)) { // Late days start after grace period
            long lateDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);

            HPLateFeeCalculation lateFee = new HPLateFeeCalculation();
            lateFee.setHpRepaymentSchedule(schedule);
            lateFee.setLateDays((int) lateDays);

            // Calculate late fee based on overdue days
            BigDecimal interestLateFee = schedule.getInterestODAmount().multiply(BigDecimal.valueOf(0.001))
                    .multiply(BigDecimal.valueOf(lateDays));
            // Calculate Principal Late Fee (e.g., 0.1% of Principal OD Amount)
            BigDecimal principalLateFee = schedule.getPrincipalODAmount().multiply(BigDecimal.valueOf(0.001))
                    .multiply(BigDecimal.valueOf(lateDays));
            lateFee.setInterestLateFee(interestLateFee);
            lateFee.setPrincipalLateFee(principalLateFee);
            lateFeeRepo.save(lateFee);
        }
    }
}

