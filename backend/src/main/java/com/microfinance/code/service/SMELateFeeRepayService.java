package com.microfinance.code.service;

import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.SMELateFeeCalculation;
import com.microfinance.code.model.SMELateFeeTracking;
import com.microfinance.code.model.SMERepaymentSchedule;
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
    @Transactional
    @Scheduled(cron = "0 * 9-23 * * *")
    public void processLateFees() {
        System.out.println("=========================Late Fee Process=============================");
        // Step 1: Retrieve all distinct SME loans with late fees
        List<Integer> smeLoanIds = lateFeeCalculationRepo.findDistinctSmeLoanIds();
        System.out.println(smeLoanIds);
        for (Integer smeLoanId : smeLoanIds) {
            List<SMELateFeeCalculation> lateFees = lateFeeCalculationRepo.findBySmeLoanId(smeLoanId);
            System.out.println("Total Late Fees Rows: "+lateFees);
            BigDecimal totalLateFees = lateFees.stream()
                    .map(SMELateFeeCalculation::getLateFees)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            System.out.println("Late FEEs total "+totalLateFees);
            CurrentAccount account = smeLoanRepo.findCurrentAccountBySmeLoanId(smeLoanId)
                    .orElseThrow(()->new NotFoundException("Cannot Find Current Account By SME Loan ID"));
            BigDecimal transactionAmount = getAvailableTransactionAmount(account); // Fetch available funds
            System.out.println("TransactionAMount : "+transactionAmount);
            if (transactionAmount.compareTo(totalLateFees) >= 0) {
                // Full payment scenario
                lateFeeCalculationRepo.deleteBySmeLoanId(smeLoanId);
                recordLateFeeTracking(smeLoanId, lateFees);
                updateRepaymentSchedules(lateFees);
                updateAccountBalance(account, totalLateFees);
            } else {
                // Insufficient funds - Update late days and fees for ALL entries
                for (SMELateFeeCalculation lateFee : lateFees) {
                    // 1. Increment late days by 1
                    lateFee.setLateDays(lateFee.getLateDays() + 1);

                    // 2. Calculate additional fee (0.1% of interest_OD_amount)
                    SMERepaymentSchedule schedule = lateFee.getSmeRepaymentSchedule();
                    BigDecimal odAmount = schedule.getInterestODAmount() != null
                            ? schedule.getInterestODAmount()
                            : BigDecimal.ZERO;

                    // Calculate 0.1% of OD amount with proper rounding
                    BigDecimal additionalFee = odAmount.multiply(new BigDecimal("0.001"))
                            .setScale(2, RoundingMode.HALF_UP);

                    // 3. Update late fees with the new calculation
                    lateFee.setLateFees(lateFee.getLateFees().add(additionalFee));

                    // 4. Save the updated entry
                    lateFeeCalculationRepo.save(lateFee);
                }






                // Partial payment scenario
//                BigDecimal remainingAmount = transactionAmount;
//                for (SMELateFeeCalculation lateFee : lateFees) {
//                    if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) break;
//
//                    if (remainingAmount.compareTo(lateFee.getLateFees()) >= 0) {
//                        remainingAmount = remainingAmount.subtract(lateFee.getLateFees());
//                        lateFeeCalculationRepo.delete(lateFee);
//                    } else {
//                        lateFee.setLateDays(lateFee.getLateDays() + 1);
//                        lateFee.setLateFees(lateFee.getLateFees().subtract(remainingAmount));
//                        lateFeeCalculationRepo.save(lateFee);
//                        remainingAmount = BigDecimal.ZERO;
//                    }
//                }
            }
        }
    }
    private BigDecimal calculateAvailableFunds(CurrentAccount account) {
        return transactionRepo.findByCurrentAccountIdAndDate(account, LocalDate.now())
                .stream()
                .map(transaction -> transaction.getType() == transactionType.CR
                        ? transaction.getAmount()
                        : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private void recordLateFeeTracking(Integer smeLoanId, List<SMELateFeeCalculation> lateFees) {
        SMELateFeeTracking tracking = new SMELateFeeTracking();
        tracking.setSmeLoan(smeLoanRepo.findById(smeLoanId).orElseThrow(()->new RuntimeException("AAA")));
        tracking.setTotalLateFees(lateFees.stream()
                .map(SMELateFeeCalculation::getLateFees)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
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
}

