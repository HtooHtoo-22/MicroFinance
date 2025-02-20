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
    @Autowired
    private CurrentAccountRepository currentAccountRepo;
    @Transactional
    @Scheduled(cron = "0 * 9-23 * * *")
    public void processLateFees() {
        System.out.println("=========================Late Fee Process=============================");

        List<Integer> smeLoanIds = lateFeeCalculationRepo.findDistinctSmeLoanIds();
        System.out.println(smeLoanIds);

        for (Integer smeLoanId : smeLoanIds) {
            List<SMELateFeeCalculation> lateFees = lateFeeCalculationRepo.findBySmeLoanId(smeLoanId);
            System.out.println("Total Late Fees Rows: " + lateFees);

            BigDecimal totalLateFees = lateFees.stream()
                    .map(SMELateFeeCalculation::getLateFees)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            System.out.println("Late Fees Total: " + totalLateFees);

            CurrentAccount account = smeLoanRepo.findCurrentAccountBySmeLoanId(smeLoanId)
                    .orElseThrow(() -> new NotFoundException("Cannot Find Current Account By SME Loan ID"));

            BigDecimal transactionAmount = getAvailableTransactionAmount(account); // Fetch available funds
            System.out.println("Transaction Amount: " + transactionAmount);

            // Fetch any existing held amount from the previous transaction
            SMELateFeeHolding lateFeeHolding = lateFeeHoldingRepo.findBySmeLoan_Id(smeLoanId).orElse(null);
            BigDecimal heldAmount = lateFeeHolding != null ? lateFeeHolding.getHoldAmount() : BigDecimal.ZERO;

            // Combine current available amount with previously held amount
            BigDecimal totalAvailableAmount = transactionAmount.add(heldAmount);
            System.out.println("Total Available (Transaction + Hold): " + totalAvailableAmount);

            if (totalAvailableAmount.compareTo(totalLateFees) >= 0) {
                // Full payment scenario
                lateFeeCalculationRepo.deleteBySmeLoanId(smeLoanId);
                recordLateFeeTracking(smeLoanId, lateFees);
                updateRepaymentSchedules(lateFees);
                updateAccountBalance(account, totalLateFees);

                // Deduct the total late fee from available funds
                totalAvailableAmount = totalAvailableAmount.subtract(totalLateFees);

                // If we had a hold amount, clear it
                if (lateFeeHolding != null) {
                    lateFeeHoldingRepo.delete(lateFeeHolding);
                }

                odService.processODRepayment(totalAvailableAmount, smeLoanId);

            } else {
                // Insufficient funds case
                BigDecimal holdAmount = totalAvailableAmount; // We hold what was paid
                System.out.println("Amount Held: " + holdAmount);


                // Deduct the available funds from the account
                updateAccountBalance(account, totalAvailableAmount);

                // Update late fee calculations (increment late days, add more fees)
                for (SMELateFeeCalculation lateFee : lateFees) {
                    lateFee.setLateDays(lateFee.getLateDays() + 1);
                    BigDecimal additionalFee = lateFee.getSmeRepaymentSchedule().getInterestODAmount()
                            .multiply(new BigDecimal("0.001"))
                            .setScale(2, RoundingMode.HALF_UP);
                    lateFee.setLateFees(lateFee.getLateFees().add(additionalFee));
                    lateFeeCalculationRepo.save(lateFee);
                }

                // Store the remaining unpaid late fee in `SMELateFeeHolding`
                if (lateFeeHolding == null) {
                    lateFeeHolding = new SMELateFeeHolding();
                    lateFeeHolding.setSmeLoan(smeLoanRepo.findById(smeLoanId).orElseThrow(() ->
                            new NotFoundException("SME Loan not found")));
                }
                lateFeeHolding.setHoldAmount(holdAmount);

                lateFeeHoldingRepo.save(lateFeeHolding);



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

