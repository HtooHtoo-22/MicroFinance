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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
            BigDecimal interestAmount = calculateInterest(smeLoan.getLoanAmount(), interestRate, dueDate);
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
            BigDecimal newInterest = calculateInterest(changedPrincipal,interestRate,schedule.getDueDate());
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

    private BigDecimal calculateInterest(BigDecimal loanAmount, BigDecimal interestRate, LocalDate dueDate) {
        // 1. Get the number of days in the current month
        LocalDate previousMonthDueDate = dueDate.minusMonths(1);
        int daysBetween = (int) ChronoUnit.DAYS.between(previousMonthDueDate, dueDate);
//        int daysInMonth = dueDate.getMonth().length(dueDate.isLeapYear());
//        int totalDays = getTotalDaysForLoanDuration(duration, dueDate);

        // 2. Annual Rate to Daily Rate = (interestRate / 100) / 365
        BigDecimal dailyRate = interestRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);

        // 3. Interest = LoanAmount × DailyRate × DaysInMonth
        BigDecimal interest = loanAmount.multiply(dailyRate)
                .multiply(BigDecimal.valueOf(daysBetween));

        return interest.setScale(2, RoundingMode.HALF_UP); // Round to 2 decimal places
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






    @Override
    public byte[] generateReport(Integer smeLoanId) throws JRException, IOException {
        try {
            // Query repayment schedule data
            List<Map<String, Object>> repaymentData = jdbcTemplate.queryForList(
                    "SELECT term_number, due_date, grace_period_end_date, total_days, " +
                            "principal, interest_od_amount, total_repaid_amount, status " +
                            "FROM sme_repayment_schedule WHERE sme_loan_id = ? AND status='FULL_OVERDUE'",
                    smeLoanId
            );

            // Query late fee calculation data
            Map<String, Object> lateFeeData = jdbcTemplate.queryForMap(
                    "SELECT late_days, late_fees FROM sme_late_fee_calculation " +
                            "WHERE sme_repayment_schedule_id = ?", smeLoanId
            );

            Integer lateDays = (Integer) lateFeeData.get("late_days");

            String rateType = (lateDays != null && lateDays <= 90) ? "SME Late Fee Before 90 Days" : "SME Late Fee After 90 Days";

            List<BigDecimal> rateList = jdbcTemplate.queryForList(
                    "SELECT value FROM rate WHERE rate_type = ?",
                    new Object[]{rateType},
                    BigDecimal.class
            );

            BigDecimal rateValue = rateList.isEmpty() ? BigDecimal.ZERO : rateList.get(0);

            if (rateValue.equals(BigDecimal.ZERO)) {
                System.out.println("Warning: No rate value found for " + rateType);
            }

            // Query late fee holding amount data
            Map<String, Object> holdAmountData = jdbcTemplate.queryForMap(
                    "SELECT hold_amount FROM sme_late_fee_holding WHERE sme_loan_id = ?", smeLoanId
            );

            // Calculate the total interest overdue
            BigDecimal totalInterestOD = BigDecimal.valueOf(
                    jdbcTemplate.queryForObject(
                            "SELECT SUM(interest_od_amount) FROM sme_repayment_schedule WHERE sme_loan_id = ?",
                            Double.class, smeLoanId
                    )
            );


            // Compute Late Fee
            BigDecimal calculatedLateFee = rateValue.multiply(totalInterestOD).setScale(2, RoundingMode.HALF_UP);



            // Prepare parameters for the report
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("repaymentData", new JRBeanCollectionDataSource(repaymentData));
            parameters.put("lateDays", lateFeeData.get("late_days"));
            parameters.put("lateFees", lateFeeData.get("late_fees"));
            parameters.put("holdAmount", holdAmountData.get("hold_amount"));
            parameters.put("totalInterestOD", totalInterestOD.setScale(2, RoundingMode.HALF_UP));
            parameters.put("calculatedLateFee", calculatedLateFee.setScale(2, RoundingMode.HALF_UP));
            parameters.put("rateValues", rateValue);
            // Compile and fill the report
            InputStream reportStream = getClass().getResourceAsStream("/reports/SMELoanRepaymentReport.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(repaymentData));

            // Return the report as a PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            // Log the error and rethrow it
            e.printStackTrace();
            throw new JRException("Error generating report", e);
        }
    }





}
