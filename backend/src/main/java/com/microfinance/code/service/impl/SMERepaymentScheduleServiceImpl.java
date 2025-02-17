package com.microfinance.code.service.impl;

import com.microfinance.code.model.SMELoan;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.repository.HolidayRepository;
import com.microfinance.code.repository.SMELoanRepo;
import com.microfinance.code.repository.SMERepaymentScheduleRepo;
import com.microfinance.code.service.interFace.SMERepaymentScheduleService;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class SMERepaymentScheduleServiceImpl implements SMERepaymentScheduleService {
    @Autowired
    private SMERepaymentScheduleRepo smeRepaymentScheduleRepo;
    @Autowired
    private HolidayRepository holidayRepo;
    @Autowired
    private SMELoanRepo smeLoanRepo;
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
            // 4. Calculate interest amount
            BigDecimal interestAmount = calculateInterest(smeLoan.getLoanAmount(), smeLoan.getInterestRate(), dueDate);
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
            int daysInMonth = dueDate.getMonth().length(dueDate.isLeapYear());
            schedule.setTotalDays(daysInMonth);
            schedule.setPrincipal(smeLoan.getPrincipal()); // Update based on your logic
            schedule.setStatus(RepaymentStatus.NOT_DUE_YET);

            // Save schedule to database (Assuming you have a repository)
            smeRepaymentScheduleRepo.save(schedule);
        }
    }

    private BigDecimal calculateInterest(BigDecimal loanAmount, BigDecimal interestRate, LocalDate dueDate) {
        // Get number of days in the month
        int daysInMonth = dueDate.getMonth().length(dueDate.isLeapYear());

        // Interest Formula: (Loan Amount * Interest Rate / 100) / 365 * Days in Month
        return loanAmount.multiply(interestRate)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(daysInMonth));
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
