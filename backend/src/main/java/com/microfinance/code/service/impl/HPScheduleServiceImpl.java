package com.microfinance.code.service.impl;

import com.microfinance.code.dto.HPScheduleDTO;
import com.microfinance.code.mapper.HPScheduleMapper;
import com.microfinance.code.model.HPLoan;
import com.microfinance.code.model.HPSchedule;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.interFace.HPScheduleService;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    @Autowired
    private HPScheduleMapper hpScheduleMapper;

    @Override
    public void createSchedule(HPLoan hpLoan) {
        System.out.println("Schedule start");
        BigDecimal totalInstallmentAmount = calculateEMI(hpLoan);
        System.out.println("Total Installment Amount : "+totalInstallmentAmount);
        BigDecimal bmfRate = getBMFRate(hpLoan, totalInstallmentAmount).divide(BigDecimal.valueOf(100));
        BigDecimal remainingAmount = hpLoan.getLoanAmount();
        int totalTerms = hpLoan.getTenor() * 12;
        LocalDate adjustedDueDate = LocalDate.now().plusMonths(1).withDayOfMonth(5);
        boolean firstTermAdjusted = false;

        for (int termNumber = 1; termNumber <= totalTerms; termNumber++) {
            BigDecimal interestAmount = remainingAmount.multiply(bmfRate);
            BigDecimal principalAmount = totalInstallmentAmount.subtract(interestAmount);
            remainingAmount = remainingAmount.subtract(principalAmount);
            LocalDate dueDate = adjustForHoliday(adjustedDueDate.plusMonths(termNumber - 1));
            HPSchedule schedule = new HPSchedule();

            if (!firstTermAdjusted && ChronoUnit.DAYS.between(LocalDate.now(), dueDate) <= 15) {
                dueDate = adjustForHoliday(dueDate.plusMonths(1));
                BigDecimal extraInterest = calculateExtraInterest(hpLoan, remainingAmount, dueDate);
                schedule.setInterestAmount(interestAmount.add(extraInterest));
                schedule.setInstallment(totalInstallmentAmount.add(extraInterest));
                firstTermAdjusted = true;
                adjustedDueDate = dueDate;
            } else {
                schedule.setInterestAmount(interestAmount);
                schedule.setInstallment(totalInstallmentAmount);
            }

            schedule.setDueDate(dueDate);
            if (hpLoan.getGracePeriod() > 0) {
                schedule.setGracePeriodEndDate(dueDate.plusDays(hpLoan.getGracePeriod()));
            }

            if (termNumber == hpLoan.getDuration()) {
                hpLoan.setEndDate(adjustForHoliday(schedule.getGracePeriodEndDate() != null ?
                        schedule.getGracePeriodEndDate() : dueDate));
                hpLoanRepo.save(hpLoan);
            }

            schedule.setHpLoan(hpLoan);
            schedule.setTermNumber(termNumber);
            schedule.setTotalDays((int) ChronoUnit.DAYS.between(dueDate.minusMonths(1), dueDate));
            schedule.setPrincipal(principalAmount);
            schedule.setTotalRepaidAmount(BigDecimal.ZERO);
            schedule.setStatus(RepaymentStatus.NOT_DUE_YET);
            hpScheduleRepo.save(schedule);
        }
    }

    private BigDecimal getBMFRate(HPLoan hpLoan, BigDecimal installmentAmount) {
        System.out.println("BMF Rate");
        BigDecimal loanAmount = hpLoan.getLoanAmount();
        BigDecimal totalMonths = BigDecimal.valueOf(hpLoan.getTenor() * 12);
        BigDecimal low = BigDecimal.ZERO, high = BigDecimal.ONE, mid;
        BigDecimal epsilon = new BigDecimal("0.0000001");

        while (high.subtract(low).compareTo(epsilon) > 0) {
            mid = low.add(high).divide(BigDecimal.valueOf(2), 15, RoundingMode.HALF_UP);
            BigDecimal powTerm = (BigDecimal.ONE.add(mid)).pow(totalMonths.intValue());
            BigDecimal calculatedPayment = loanAmount.multiply(mid)
                    .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(powTerm, 15, RoundingMode.HALF_UP)), 15, RoundingMode.HALF_UP);
            if (calculatedPayment.compareTo(installmentAmount) > 0) high = mid;
            else low = mid;
        }
        return low.multiply(BigDecimal.valueOf(100)).setScale(5, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateEMI(HPLoan hpLoan) {
        System.out.println("Calculate EMI");
        BigDecimal principal = hpLoan.getLoanAmount();
        System.out.println("Principal : "+principal);
        BigDecimal annualInterestRate = hpLoan.getInterestRate().divide(BigDecimal.valueOf(100));
        System.out.println("Annual Rate : "+annualInterestRate);
        BigDecimal totalInterest = principal.multiply(annualInterestRate).multiply(BigDecimal.valueOf(hpLoan.getTenor()));
        System.out.println("Interest : "+totalInterest);
        return principal.add(totalInterest).divide(BigDecimal.valueOf(hpLoan.getTenor() * 12), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateExtraInterest(HPLoan hpLoan, BigDecimal remainingAmount, LocalDate dueDate) {
        System.out.println("Calculate Extra Interest");
        long extraDays = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        return remainingAmount.multiply(hpLoan.getInterestRate().divide(BigDecimal.valueOf(100)))
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(extraDays));
    }

    private LocalDate adjustForHoliday(LocalDate dueDate) {
        while (holidayRepo.existsByHolidayDate(dueDate)) {
            dueDate = dueDate.plusDays(1);
        }
        return dueDate;
    }

    @Override
    public List<HPScheduleDTO> getSchedulesByLoanId(Integer hpLoanId) {
        List<HPSchedule> schedules = hpScheduleRepo.findByHpLoanId(hpLoanId);
        return hpScheduleMapper.toDTOList(schedules);
    }
}
