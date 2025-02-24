package com.microfinance.code.service;

import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.status.transactionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class SMELateFeeRepayService {

    @Autowired
    private SMELateFeeCalculationRepo lateFeeCalculationRepo;

    @Autowired
    private SMELateFeeTrackingRepo lateFeeTrackingRepo;

    @Autowired
    private SMERepaymentScheduleRepo repaymentScheduleRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private SMELoanRepo smeLoanRepo;

    @Autowired
    private CurrentAccountRepository accountRepo;

    @Autowired
    private SMEODRepayService odService;

    @Autowired
    private SMELateFeeHoldingRepo lateFeeHoldingRepo;

    @Transactional
    @Scheduled(initialDelay = 0, fixedRate = Long.MAX_VALUE)
    public void processLateFees() {
        logProcessStart();

        List<Integer> smeLoanIds = lateFeeCalculationRepo.findDistinctSmeLoanIds();
        logSmeLoanIds(smeLoanIds);

        for (Integer smeLoanId : smeLoanIds) {
            processLateFeesForLoan(smeLoanId);
        }
        System.out.println("=============================================================================");
    }

    private void processLateFeesForLoan(Integer smeLoanId) {
        List<SMELateFeeCalculation> lateFees = lateFeeCalculationRepo.findBySmeLoanId(smeLoanId);
        logLateFees(lateFees);

        BigDecimal totalLateFees = calculateTotalLateFees(lateFees);
        logTotalLateFees(totalLateFees);

        CurrentAccount account = fetchCurrentAccountBySmeLoanId(smeLoanId);
        BigDecimal availableFunds = getAvailableTransactionAmount(account);
        logAvailableFunds(availableFunds);

        SMELateFeeHolding lateFeeHolding = fetchLateFeeHolding(smeLoanId);
        BigDecimal heldAmount = getHeldAmount(lateFeeHolding);

        BigDecimal totalAvailableAmount = availableFunds.add(heldAmount);
        logTotalAvailableAmount(totalAvailableAmount);

        if (totalAvailableAmount.compareTo(totalLateFees) >= 0) {
            handleFullPayment(smeLoanId, lateFees, account, totalLateFees, totalAvailableAmount, lateFeeHolding);
        } else {
            handlePartialPayment(smeLoanId, lateFees, account, totalAvailableAmount, lateFeeHolding);
        }
    }

    private void handleFullPayment(Integer smeLoanId, List<SMELateFeeCalculation> lateFees, CurrentAccount account,
                                   BigDecimal totalLateFees, BigDecimal totalAvailableAmount, SMELateFeeHolding lateFeeHolding) {
        lateFeeCalculationRepo.deleteBySmeLoanId(smeLoanId);
        recordLateFeeTracking(smeLoanId, lateFees);
        updateRepaymentSchedules(lateFees);
        updateAccountBalance(account, totalLateFees);

        totalAvailableAmount = totalAvailableAmount.subtract(totalLateFees);

        if (lateFeeHolding != null) {
            lateFeeHoldingRepo.delete(lateFeeHolding);
        }

        odService.processODRepayment(totalAvailableAmount, smeLoanId);
    }

    private void handlePartialPayment(Integer smeLoanId, List<SMELateFeeCalculation> lateFees, CurrentAccount account,
                                      BigDecimal totalAvailableAmount, SMELateFeeHolding lateFeeHolding) {
        BigDecimal holdAmount = totalAvailableAmount;
        logAmountHeld(holdAmount);

        updateAccountBalance(account, totalAvailableAmount);
        incrementLateDaysAndFees(lateFees);

        if (lateFeeHolding == null) {
            lateFeeHolding = createLateFeeHolding(smeLoanId);
        }
        lateFeeHolding.setHoldAmount(holdAmount);
        lateFeeHoldingRepo.save(lateFeeHolding);
    }

    private SMELateFeeHolding createLateFeeHolding(Integer smeLoanId) {
        SMELateFeeHolding lateFeeHolding = new SMELateFeeHolding();
        lateFeeHolding.setSmeLoan(smeLoanRepo.findById(smeLoanId)
                .orElseThrow(() -> new NotFoundException("SME Loan not found")));
        return lateFeeHolding;
    }

    private void incrementLateDaysAndFees(List<SMELateFeeCalculation> lateFees) {
        for (SMELateFeeCalculation lateFee : lateFees) {
            lateFee.setLateDays(lateFee.getLateDays() + 1);
            BigDecimal additionalFee = calculateAdditionalFee(lateFee);
            lateFee.setLateFees(lateFee.getLateFees().add(additionalFee));
            lateFeeCalculationRepo.save(lateFee);
        }
    }

    private BigDecimal calculateAdditionalFee(SMELateFeeCalculation lateFee) {
        return lateFee.getSmeRepaymentSchedule().getInterestODAmount()
                .multiply(new BigDecimal("0.001"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private CurrentAccount fetchCurrentAccountBySmeLoanId(Integer smeLoanId) {
        return smeLoanRepo.findCurrentAccountBySmeLoanId(smeLoanId)
                .orElseThrow(() -> new NotFoundException("Cannot Find Current Account By SME Loan ID"));
    }

    private SMELateFeeHolding fetchLateFeeHolding(Integer smeLoanId) {
        return lateFeeHoldingRepo.findBySmeLoan_Id(smeLoanId).orElse(null);
    }

    private BigDecimal getHeldAmount(SMELateFeeHolding lateFeeHolding) {
        return lateFeeHolding != null ? lateFeeHolding.getHoldAmount() : BigDecimal.ZERO;
    }

    private BigDecimal calculateTotalLateFees(List<SMELateFeeCalculation> lateFees) {
        return lateFees.stream()
                .map(SMELateFeeCalculation::getLateFees)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void recordLateFeeTracking(Integer smeLoanId, List<SMELateFeeCalculation> lateFees) {
        SMELateFeeTracking tracking = new SMELateFeeTracking();
        tracking.setSmeLoan(smeLoanRepo.findById(smeLoanId)
                .orElseThrow(() -> new RuntimeException("SME Loan not found")));
        tracking.setTotalLateFees(calculateTotalLateFees(lateFees));
        tracking.setLateDays(lateFees.stream().mapToInt(SMELateFeeCalculation::getLateDays).max().orElse(0));
        tracking.setLateFeeRepaidDate(LocalDate.now());
        lateFeeTrackingRepo.save(tracking);
    }

    private void updateRepaymentSchedules(List<SMELateFeeCalculation> lateFees) {
        for (SMELateFeeCalculation lateFee : lateFees) {
            SMERepaymentSchedule schedule = lateFee.getSmeRepaymentSchedule();
            schedule.setLateFeeStatus(false);
            repaymentScheduleRepo.save(schedule);
        }
    }

    private BigDecimal getAvailableTransactionAmount(CurrentAccount account) {
        return transactionRepo.findByCurrentAccountIdAndDate(account, LocalDate.now())
                .stream()
                .map(transaction -> transaction.getType() == transactionType.CR
                        ? transaction.getAmount()
                        : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateAccountBalance(CurrentAccount account, BigDecimal amountToRepay) {
        account.setTotalBalence(account.getTotalBalence() - amountToRepay.doubleValue());
        accountRepo.save(account);
    }

    private void logProcessStart() {
        System.out.println("=========================Late Fee Process=============================");
    }

    private void logSmeLoanIds(List<Integer> smeLoanIds) {
        System.out.println(smeLoanIds);
    }

    private void logLateFees(List<SMELateFeeCalculation> lateFees) {
        System.out.println("Total Late Fees Rows: " + lateFees);
    }

    private void logTotalLateFees(BigDecimal totalLateFees) {
        System.out.println("Late Fees Total: " + totalLateFees);
    }

    private void logAvailableFunds(BigDecimal availableFunds) {
        System.out.println("Transaction Amount: " + availableFunds);
    }

    private void logTotalAvailableAmount(BigDecimal totalAvailableAmount) {
        System.out.println("Total Available (Transaction + Hold): " + totalAvailableAmount);
    }

    private void logAmountHeld(BigDecimal holdAmount) {
        System.out.println("Amount Held: " + holdAmount);
    }
}