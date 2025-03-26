package com.microfinance.code.service.impl;

import com.microfinance.code.dto.LoanDashboardDTO;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.interFace.LoanDashboardService;
import com.microfinance.code.status.LoanStatus;
import com.microfinance.code.status.RepaymentStatus;
import com.microfinance.code.status.transactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanDashboardServiceImpl implements LoanDashboardService {

    private final SMELoanRepo smeLoanRepository;
    private final HPLoanRepo hpLoanRepo;
    private final SMERepaymentScheduleRepo smeRepaymentScheduleRepo;
    private final HPScheduleRepo hpScheduleRepo;
    private final TransactionRepository transactionRepository;

    @Override
    public LoanDashboardDTO getLoanDashboardMetrics(LocalDate startDate, LocalDate endDate) {
        LoanDashboardDTO dashboard = new LoanDashboardDTO();
        dashboard.setStartDate(startDate);
        dashboard.setEndDate(endDate);

        dashboard.setNetCashFlow(calculateNetCashFlow(startDate, endDate));
        dashboard.setTotalDisbursements(calculateTotalLoanDisbursements(startDate, endDate));
        dashboard.setTotalRepayments(calculateTotalRepayments(startDate, endDate));
        dashboard.setOutstandingPortfolio(calculateOutstandingLoanPortfolio());
        dashboard.setDelinquencyRate(calculateDelinquencyRate());

        // Additional breakdowns
        dashboard.setSmeDisbursements(getSmeDisbursements(startDate, endDate));
        dashboard.setHpDisbursements(getHpDisbursements(startDate, endDate));
        dashboard.setSmeRepayments(getSmeRepayments(startDate, endDate));
        dashboard.setHpRepayments(getHpRepayments(startDate, endDate));

        return dashboard;
    }

    private BigDecimal calculateNetCashFlow(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal outflow = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getType() == transactionType.CR) {
                income = income.add(t.getAmount());
            } else if (t.getType() == transactionType.DR) {
                outflow = outflow.add(t.getAmount());
            }
        }

        return income.subtract(outflow);
    }

    private BigDecimal calculateTotalLoanDisbursements(LocalDate startDate, LocalDate endDate) {
        BigDecimal smeDisbursements = getSmeDisbursements(startDate, endDate);
        BigDecimal hpDisbursements = getHpDisbursements(startDate, endDate);
        return smeDisbursements.add(hpDisbursements);
    }

    private BigDecimal calculateTotalRepayments(LocalDate startDate, LocalDate endDate) {
        BigDecimal smeRepayments = getSmeRepayments(startDate, endDate);
        BigDecimal hpRepayments = getHpRepayments(startDate, endDate);
        return smeRepayments.add(hpRepayments);
    }

    private BigDecimal calculateOutstandingLoanPortfolio() {
        BigDecimal smeOutstanding = calculateSMEOutstanding();
        BigDecimal hpOutstanding = calculateHPOutstanding();
        return smeOutstanding.add(hpOutstanding);
    }

    private BigDecimal calculateDelinquencyRate() {
        BigDecimal totalOutstanding = calculateOutstandingLoanPortfolio();

        if (totalOutstanding.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal delinquentAmount = calculateSMEDelinquent().add(calculateHPDelinquent());

        return delinquentAmount.divide(totalOutstanding, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    // Helper methods
    private BigDecimal getSmeDisbursements(LocalDate startDate, LocalDate endDate) {
        List<SMELoan> loans = smeLoanRepository.findByApprovedDateBetweenAndStatus(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59), LoanStatus.APPROVE);

        return loans.stream()
                .map(SMELoan::getLoanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getHpDisbursements(LocalDate startDate, LocalDate endDate) {
        List<HPLoan> loans = hpLoanRepo.findByApprovedDateBetweenAndStatus(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59), LoanStatus.APPROVE);

        return loans.stream()
                .map(HPLoan::getLoanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getSmeRepayments(LocalDate startDate, LocalDate endDate) {
        List<SMERepaymentSchedule> schedules = smeRepaymentScheduleRepo
                .findByFullyPaidDateBetweenAndStatus(startDate, endDate, RepaymentStatus.PAID);

        return schedules.stream()
                .map(s -> s.getTotalRepaidAmount() != null ? s.getTotalRepaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getHpRepayments(LocalDate startDate, LocalDate endDate) {
        List<HPSchedule> schedules = hpScheduleRepo
                .findByFullyPaidDateBetweenAndStatus(startDate, endDate, RepaymentStatus.PAID);

        return schedules.stream()
                .map(s -> s.getTotalRepaidAmount() != null ? s.getTotalRepaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateSMEOutstanding() {
        List<SMELoan> loans = smeLoanRepository.findByStatus(LoanStatus.APPROVE);
        BigDecimal total = BigDecimal.ZERO;

        for (SMELoan loan : loans) {
            List<SMERepaymentSchedule> schedules = smeRepaymentScheduleRepo
                    .findBySmeLoanAndStatusIn(loan, List.of(
                            RepaymentStatus.NOT_DUE_YET,
                            RepaymentStatus.IN_GRACE_PERIOD,
                            RepaymentStatus.FULL_OVERDUE,
                            RepaymentStatus.PARTIAL_OVERDUE));

            for (SMERepaymentSchedule schedule : schedules) {
                BigDecimal remaining = schedule.getPrincipal()
                        .subtract(schedule.getTotalRepaidAmount() != null ?
                                schedule.getTotalRepaidAmount() : BigDecimal.ZERO);
                total = total.add(remaining);
            }
        }
        return total;
    }

    private BigDecimal calculateHPOutstanding() {
        List<HPLoan> loans = hpLoanRepo.findByStatus(LoanStatus.APPROVE);
        BigDecimal total = BigDecimal.ZERO;

        for (HPLoan loan : loans) {
            List<HPSchedule> schedules = hpScheduleRepo
                    .findByHpLoanAndStatusIn(loan, List.of(
                            RepaymentStatus.NOT_DUE_YET,
                            RepaymentStatus.IN_GRACE_PERIOD,
                            RepaymentStatus.FULL_OVERDUE,
                            RepaymentStatus.PARTIAL_OVERDUE));

            for (HPSchedule schedule : schedules) {
                BigDecimal remaining = schedule.getPrincipal()
                        .subtract(schedule.getTotalRepaidAmount() != null ?
                                schedule.getTotalRepaidAmount() : BigDecimal.ZERO);
                total = total.add(remaining);
            }
        }
        return total;
    }

    private BigDecimal calculateSMEDelinquent() {
        List<SMELoan> loans = smeLoanRepository.findByStatus(LoanStatus.APPROVE);
        BigDecimal total = BigDecimal.ZERO;

        for (SMELoan loan : loans) {
            List<SMERepaymentSchedule> schedules = smeRepaymentScheduleRepo
                    .findBySmeLoanAndStatusIn(loan, List.of(
                            RepaymentStatus.FULL_OVERDUE,
                            RepaymentStatus.PARTIAL_OVERDUE));

            for (SMERepaymentSchedule schedule : schedules) {
                BigDecimal remaining = schedule.getPrincipal()
                        .subtract(schedule.getTotalRepaidAmount() != null ?
                                schedule.getTotalRepaidAmount() : BigDecimal.ZERO);
                total = total.add(remaining);
            }
        }
        return total;
    }

    private BigDecimal calculateHPDelinquent() {
        List<HPLoan> loans = hpLoanRepo.findByStatus(LoanStatus.APPROVE);
        BigDecimal total = BigDecimal.ZERO;

        for (HPLoan loan : loans) {
            List<HPSchedule> schedules = hpScheduleRepo
                    .findByHpLoanAndStatusIn(loan, List.of(
                            RepaymentStatus.FULL_OVERDUE,
                            RepaymentStatus.PARTIAL_OVERDUE));

            for (HPSchedule schedule : schedules) {
                BigDecimal remaining = schedule.getPrincipal()
                        .subtract(schedule.getTotalRepaidAmount() != null ?
                                schedule.getTotalRepaidAmount() : BigDecimal.ZERO);
                total = total.add(remaining);
            }
        }
        return total;
    }
}