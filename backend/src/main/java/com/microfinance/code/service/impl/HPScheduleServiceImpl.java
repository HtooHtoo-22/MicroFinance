package com.microfinance.code.service.impl;

import com.microfinance.code.model.HPLoan;
import com.microfinance.code.model.HPSchedule;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.interFace.HPScheduleService;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class HPScheduleServiceImpl implements HPScheduleService {
    @Autowired
    private SMERepaymentScheduleRepo smeRepaymentScheduleRepo;
    @Autowired
    private HolidayRepository holidayRepo;
    @Autowired
    private SMELoanRepo smeLoanRepo;
    @Autowired
    private RateRepository rateRepo;
    @Autowired
    private HPScheduleRepo hpScheduleRepo;
    @Autowired
    private HPLoanRepo hpLoanRepo;
    @Override
    public void createSchedule(HPLoan hpLoan) {
        // Initialize necessary variables
        BigDecimal totalInstallmentAmount = calculateEMI(hpLoan);
        BigDecimal bmfRate = getBMFRate(hpLoan,totalInstallmentAmount).divide(BigDecimal.valueOf(100)); // Implement logic to get BMF rate
        BigDecimal remainingAmount = hpLoan.getLoanAmount(); // Assuming the remaining amount starts with the loan amount
         // Implement logic to calculate EMI (monthly installment)
        System.out.println("BMF Rate : "+bmfRate);
        System.out.println("Installment Amount : "+totalInstallmentAmount);
        int totalTerms = hpLoan.getTenor() * 12; // Number of terms, assumed as the tenor (in months)
        LocalDate currentDueDate = LocalDate.now().plusMonths(1); // Assuming the first due date is today, adjust as necessary
        LocalDate expiredDate = null;
        for (int termNumber = 1; termNumber <= totalTerms; termNumber++) {
            // Calculate interest and principal for this term
            BigDecimal interestAmount = remainingAmount.multiply(bmfRate); // Interest for this term
            BigDecimal principalAmount = totalInstallmentAmount.subtract(interestAmount); // Principal for this term
            remainingAmount = remainingAmount.subtract(principalAmount); // Update remaining amount after principal repayment
            // Create a new HPSchedule entity for this term
            LocalDate dueDate = currentDueDate.plusMonths(termNumber - 1);
            dueDate = adjustForHoliday(dueDate);
            LocalDate gracePeriodEndDate = null;
            if (hpLoan.getGracePeriod()>0) {
                gracePeriodEndDate = dueDate.plusDays(hpLoan.getGracePeriod());
            }
            // 6. If this is the last term, determine expired date
            if (termNumber == hpLoan.getDuration()) {
                if (gracePeriodEndDate != null) {
                    expiredDate = adjustForHoliday(gracePeriodEndDate);
                } else {
                    expiredDate = adjustForHoliday(dueDate);
                }
                hpLoan.setEndDate(expiredDate);
                hpLoanRepo.save(hpLoan);
            }
            HPSchedule schedule = new HPSchedule();




            schedule.setHpLoan(hpLoan);
            schedule.setTermNumber(termNumber);
            schedule.setDueDate(dueDate); // Increment the due date by month
            schedule.setGracePeriodEndDate(gracePeriodEndDate);
            LocalDate previousMonthDueDate = dueDate.minusMonths(1);
            int daysBetween = (int) ChronoUnit.DAYS.between(previousMonthDueDate, dueDate);
            schedule.setTotalDays(daysBetween);
            schedule.setPrincipal(principalAmount);
            schedule.setInterestAmount(interestAmount);
            schedule.setInstallment(totalInstallmentAmount); // Same for each term
            schedule.setTotalRepaidAmount(BigDecimal.ZERO); // To be updated later on repayment
            schedule.setStatus(RepaymentStatus.NOT_DUE_YET); // Initial status



            // Save the schedule in the database (assuming you have a repository)
            hpScheduleRepo.save(schedule); // Save the schedule

        }
    }

    // Example method to get BMF rate (you need to implement this)


    public BigDecimal getBMFRate(HPLoan hpLoan, BigDecimal installmentAmount) {
        // Step 1: Get the loan amount, installment amount, and the number of months
        BigDecimal loanAmount = hpLoan.getLoanAmount();
        BigDecimal monthlyInstallment = installmentAmount;
        BigDecimal totalMonths = BigDecimal.valueOf(hpLoan.getTenor() * 12);

        // Step 2: Set up binary search
        BigDecimal low = BigDecimal.ZERO;
        BigDecimal high = BigDecimal.ONE; // 100% interest rate as a decimal
        BigDecimal mid = BigDecimal.ZERO;
        BigDecimal epsilon = new BigDecimal("0.0000001"); // Precision for binary search

        // Step 3: Perform binary search to find the monthly interest rate
        while (high.subtract(low).compareTo(epsilon) > 0) {
            // Calculate the midpoint interest rate
            mid = low.add(high).divide(BigDecimal.valueOf(2), 15, RoundingMode.HALF_UP);

            // Calculate the monthly payment using the formula
            BigDecimal powTerm = (BigDecimal.ONE.add(mid)).pow(totalMonths.intValue());
            BigDecimal calculatedPayment = loanAmount.multiply(mid)
                    .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(powTerm, 15, RoundingMode.HALF_UP)), 15, RoundingMode.HALF_UP);

            // Adjust the range based on whether the calculated payment is higher or lower
            if (calculatedPayment.compareTo(monthlyInstallment) > 0) {
                high = mid; // We need a smaller interest rate
            } else {
                low = mid; // We need a larger interest rate
            }
        }

        // Convert to percentage (multiply by 100)
        BigDecimal monthlyRate = mid.multiply(BigDecimal.valueOf(100));

        // Round to 5 decimal places
        return monthlyRate.setScale(5, RoundingMode.HALF_UP);
    }




    // Example method to calculate EMI (you need to implement this)
    public BigDecimal calculateEMI(HPLoan hpLoan) {
        BigDecimal principal = hpLoan.getLoanAmount();
        BigDecimal annualInterestRate = hpLoan.getInterestRate().divide(BigDecimal.valueOf(100));
        BigDecimal tenor = BigDecimal.valueOf(hpLoan.getTenor());
        BigDecimal totalInterestAmount = principal
                .multiply(annualInterestRate)
                .multiply(tenor);
        BigDecimal allAmount = principal.add(totalInterestAmount);
        BigDecimal totalMonths = BigDecimal.valueOf(hpLoan.getTenor() * 12);

        // Specify scale and rounding mode
        int scale = 2; // Two decimal places (you can adjust this as needed)
        BigDecimal installment = allAmount.divide(totalMonths, scale, RoundingMode.HALF_UP);

        return installment;
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
