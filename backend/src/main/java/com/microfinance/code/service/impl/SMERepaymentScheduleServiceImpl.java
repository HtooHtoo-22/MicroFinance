package com.microfinance.code.service.impl;

import com.microfinance.code.dto.SMEScheduleDTO;
import com.microfinance.code.mapper.SMEScheduleMapper;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.repository.HolidayRepository;
import com.microfinance.code.repository.RateRepository;
import com.microfinance.code.repository.SMELoanRepo;
import com.microfinance.code.repository.SMERepaymentScheduleRepo;
import com.microfinance.code.service.interFace.SMERepaymentScheduleService;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SMERepaymentScheduleServiceImpl implements SMERepaymentScheduleService {
    @Autowired
    private SMERepaymentScheduleRepo smeRepaymentScheduleRepo;
    @Autowired
    private HolidayRepository holidayRepo;
    @Autowired
    private SMELoanRepo smeLoanRepo;
    @Autowired
    private RateRepository rateRepo;
    @Autowired
    private SMEScheduleMapper scheduleMapper;
    @Override
    public void createSchedule(SMELoan smeLoan) {
        // 1. Validate the input
        if (smeLoan.getApprovedDate() == null || smeLoan.getDuration() <= 0) {
            throw new IllegalArgumentException("Loan must be approved and have a valid duration.");
        }

        // 2. Determine the first due date
        LocalDate firstDueDate = smeLoan.getApprovedDate().plusMonths(1).toLocalDate(); // Change to LocalDate
        LocalDate expiredDate = null; // Variable to hold expired date

        // 3. Iterate over the loan duration to create each schedule term
        for (int term = 1; term <= smeLoan.getDuration(); term++) {
            LocalDate dueDate = firstDueDate.plusMonths(term - 1); // Monthly due date (LocalDate)
            dueDate = adjustForHoliday(dueDate);
            BigDecimal interestRate  = rateRepo.findValueByRateType("SME Loan Interest Rate");
            // 4. Calculate interest amount
            BigDecimal interestAmount = calculateInterest(smeLoan.getLoanAmount(), interestRate, dueDate,smeLoan.getDuration());
            // 4.5. Calculate grace period end date
            LocalDate gracePeriodEndDate = null;
            if (smeLoan.getGracePeriod()>0) {
                gracePeriodEndDate = dueDate.plusDays(smeLoan.getGracePeriod());
            }
            // 6. If this is the last term, determine expired date
            if (term == smeLoan.getDuration()) {
                if (gracePeriodEndDate != null) {
                    expiredDate = adjustForHoliday(gracePeriodEndDate);
                } else {
                    expiredDate = adjustForHoliday(dueDate);
                }
            }
            smeLoan.setExpiredDate(expiredDate);
            smeLoanRepo.save(smeLoan);
            // 5. Create and save schedule entry (Assuming LoanSchedule is an entity)
            SMERepaymentSchedule schedule = new SMERepaymentSchedule();
            schedule.setGracePeriodEndDate(gracePeriodEndDate);
            schedule.setSmeLoan(smeLoan);
            schedule.setTermNumber(term);
            schedule.setDueDate(dueDate); // Use LocalDate for dueDate
            schedule.setInterestAmount(interestAmount);
            int daysInMonth = dueDate.getMonth().minus(1).length(dueDate.isLeapYear());
            // If you need to calculate the exact number of days between dates (e.g., from Jan 27 to Feb 27), do the following:
            LocalDate previousMonthDueDate = dueDate.minusMonths(1);
            int daysBetween = (int) ChronoUnit.DAYS.between(previousMonthDueDate, dueDate);
            schedule.setTotalDays(daysBetween);
            schedule.setPrincipal(smeLoan.getPrincipal()); // Update based on your logic
            schedule.setStatus(RepaymentStatus.NOT_DUE_YET);

            // Save schedule to database (Assuming you have a repository)
            smeRepaymentScheduleRepo.save(schedule);
        }
    }
    @Override
    public void editSchedule(SMELoan smeLoan, BigDecimal changedPrincipal) {
        // Retrieve the schedules that are not yet due
        List<SMERepaymentSchedule> schedules = smeRepaymentScheduleRepo.findBySmeLoanAndStatus(smeLoan, RepaymentStatus.NOT_DUE_YET);

        // Iterate through the schedules and update the principal and interest
        for (SMERepaymentSchedule schedule : schedules) {
            // Update the principal in the schedule
            schedule.setPrincipal(changedPrincipal);
            System.out.println("Changed Principal : "+changedPrincipal);
            // Recalculate the interest based on the new principal
            BigDecimal interestRate  = rateRepo.findValueByRateType("SME Loan Interest Rate");
            BigDecimal newInterest = calculateInterest(changedPrincipal,interestRate,schedule.getDueDate(),smeLoan.getDuration());
            System.out.println("New interest : "+newInterest);
            // Update the interest in the schedule
            schedule.setInterestAmount(newInterest);

            // Save the updated schedule
            smeRepaymentScheduleRepo.save(schedule);
        }
    }

    @Override
    public List<SMEScheduleDTO> getSchedulesByLoanId(Integer loanId) {
        List<SMERepaymentSchedule> schedules = smeRepaymentScheduleRepo.findBySmeLoanId(loanId);
        return schedules.stream()
                .map(scheduleMapper::toDTO)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateInterest(BigDecimal loanAmount, BigDecimal interestRate, LocalDate dueDate,int duration) {
        // Get number of days in the month
        int daysInMonth = dueDate.getMonth().length(dueDate.isLeapYear());
        int totalDays = getTotalDaysForLoanDuration(duration, dueDate);
        // Interest Formula: (Loan Amount * Interest Rate / 100) / 365 * Days in Month
        return loanAmount.multiply(interestRate)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(totalDays), 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(daysInMonth));
    }
    private int getTotalDaysForLoanDuration(int durationInMonths, LocalDate startDate) {
        LocalDate endDate = startDate.plusMonths(durationInMonths);
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    private LocalDate adjustForHoliday(LocalDate dueDate) {
        while (isHoliday(dueDate)) {
            dueDate = dueDate.plusDays(1); // Move to the next day if it's a holiday
        }
        return dueDate;
    }
    private boolean isHoliday(LocalDate date) {
        return holidayRepo.existsByHolidayDate(date); // Assuming holidayRepo has a method to check for holidays
    }
}
