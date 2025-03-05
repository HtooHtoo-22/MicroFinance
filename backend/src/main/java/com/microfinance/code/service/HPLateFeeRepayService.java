package com.microfinance.code.service;

import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.status.RepaymentStatus;
import com.microfinance.code.status.transactionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HPLateFeeRepayService {
    @Autowired
    private HPLateFeeCalculationRepo lateFeeCalculationRepo;

    @Autowired
    private HPLateFeeTrackingRepo lateFeeTrackingRepo;

    @Autowired
    private HPScheduleRepo repaymentScheduleRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private HPLoanRepo hpLoanRepo;

    @Autowired
    private CurrentAccountRepository accountRepo;

    @Autowired
    private HPODRepayService odService;

    @Autowired
    private HPLateFeeHoldingRepo lateFeeHoldingRepo;

    @Autowired
    private RateRepository rateRepo;

    @Transactional
    @Scheduled(initialDelay = 0, fixedRate = Long.MAX_VALUE)
    public void processLateFees() {
        System.out.println("================================Hp late fee hehe ==================================");

        List<Integer> hpLoanIds = lateFeeCalculationRepo.findDistinctHpLoanIds();

        for (Integer hpLoanId : hpLoanIds) {
            processLateFeesForLoan(hpLoanId);
        }
        System.out.println("=============================================================================");
    }
    private void processLateFeesForLoan(Integer hpLoanId) {
        List<HPLateFeeCalculation> lateFees = lateFeeCalculationRepo.findByHpLoanId(hpLoanId);
        System.out.println("Late Fee Term List : "+lateFees);
      //  logLateFees(lateFees);

        BigDecimal totalLateFees = calculateTotalLateFees(lateFees);
        System.out.println("His Total Late Fees : "+totalLateFees);
        //logTotalLateFees(totalLateFees);

        CurrentAccount account = fetchCurrentAccountByHpLoanId(hpLoanId);
        BigDecimal availableFunds = getAvailableTransactionAmount(account);
        System.out.println("Today Transaction AMount : "+availableFunds);
        //logAvailableFunds(availableFunds);

        HPLateFeeHolding lateFeeHolding = fetchLateFeeHolding(hpLoanId);
        BigDecimal heldAmount = getHeldAmount(lateFeeHolding);
        System.out.println("Held Amount : "+heldAmount);
        BigDecimal totalAvailableAmount = availableFunds.add(heldAmount);
        System.out.println("Total Amount (Tran+Hold) : "+totalAvailableAmount);
       // logTotalAvailableAmount(totalAvailableAmount);

        if (totalAvailableAmount.compareTo(totalLateFees) >= 0) {
            handleFullPayment(hpLoanId, lateFees, account, totalLateFees, totalAvailableAmount, lateFeeHolding);
        } else {
            System.out.println("Hehehe");
            handlePartialPayment(hpLoanId, lateFees, account, totalAvailableAmount, lateFeeHolding);
        }
    }
    private void handleFullPayment(Integer hpLoanId, List<HPLateFeeCalculation> lateFees, CurrentAccount account,
                                   BigDecimal totalLateFees, BigDecimal totalAvailableAmount, HPLateFeeHolding lateFeeHolding) {
        lateFeeCalculationRepo.deleteByHpLoanId(hpLoanId);
        recordLateFeeTracking(hpLoanId, lateFees);
        updateRepaymentSchedules(lateFees);
        updateAccountBalance(account, totalLateFees);

        totalAvailableAmount = totalAvailableAmount.subtract(totalLateFees);

        if (lateFeeHolding != null) {
            lateFeeHoldingRepo.delete(lateFeeHolding);
        }

       // odService.processODRepayment(totalAvailableAmount, hpLoanId);
    }
    private void handlePartialPayment(Integer hpLoanId, List<HPLateFeeCalculation> lateFees, CurrentAccount account,
                                      BigDecimal totalAvailableAmount, HPLateFeeHolding lateFeeHolding) {

        int maxLateDays = lateFees.stream()
                .mapToInt(HPLateFeeCalculation::getLateDays)
                .max()
                .orElse(0);


        BigDecimal holdAmount = totalAvailableAmount;
        if(maxLateDays==90){
            BigDecimal totalBalance = BigDecimal.valueOf(account.getTotalBalence()); // Convert to BigDecimal
            holdAmount = totalBalance.add(holdAmount); // Add balances
            account.setTotalBalence(0.0); // Set back as Double if needed
            accountRepo.save(account);
        }
       // logAmountHeld(holdAmount);
        updateAccountBalance(account, totalAvailableAmount);
        HPLoan hpLoan = hpLoanRepo.findById(hpLoanId)
                .orElseThrow(()->new NotFoundException("HP Loan Not Found"));
        incrementLateDaysAndFees(lateFees,hpLoan);
        if (lateFeeHolding == null) {
            lateFeeHolding = createLateFeeHolding(hpLoanId);
        }
        lateFeeHolding.setHoldAmount(holdAmount);
        lateFeeHoldingRepo.save(lateFeeHolding);
    }
    @Transactional
    public void incrementLateDaysAndFees(List<HPLateFeeCalculation> lateFees, HPLoan hpLoan) {
        if (lateFees.isEmpty()) {
            return;
        }

        // Step 1: Find the max late days in the list
        int maxLateDays = lateFees.stream()
                .mapToInt(HPLateFeeCalculation::getLateDays)
                .max()
                .orElse(0);
        System.out.println("Max Late Days : " + maxLateDays);

        BigDecimal outstandingAmount = calculateOutstandingAmount(hpLoan);

        for (HPLateFeeCalculation lateFee : lateFees) {
            // Re-fetch the entity to ensure it exists
            Optional<HPLateFeeCalculation> existingFeeOpt = lateFeeCalculationRepo.findById(lateFee.getId());
            if (existingFeeOpt.isEmpty()) {
                continue; // Skip if the entity was deleted by another transaction
            }

            HPLateFeeCalculation existingFee = existingFeeOpt.get();
            existingFee.setLateDays(existingFee.getLateDays() + 1); // Increment late days

            BigDecimal additionalFee;
            if (maxLateDays >= 90 && existingFee.getLateDays() > 90) {
                // Apply additional fee for late days above 90
                BigDecimal lateFeeAfter90Rate  = rateRepo.findValueByRateType("HP Late Fee After 90 Days").divide(BigDecimal.valueOf(100));
                additionalFee = outstandingAmount.multiply(lateFeeAfter90Rate);
                System.out.println("Outstanding Amount : " + outstandingAmount);
                System.out.println("Additional Fee : " + additionalFee);

                if (maxLateDays == 90) {
                    System.out.println("Hello");
                    existingFee.setTotalLateFee(additionalFee);
                    existingFee.setInterestLateFee(BigDecimal.ZERO);
                    existingFee.setPrincipalLateFee(BigDecimal.ZERO);
                    lateFeeCalculationRepo.save(existingFee); // Save first before delete
                    lateFeeCalculationRepo.deleteOldLateFeesBySchedule(existingFee.getHpRepaymentSchedule().getHpLoan());
                    break;
                } else {
                    existingFee.setTotalLateFee(existingFee.getTotalLateFee().add(additionalFee));
                }

            } else {
                // Apply normal additional fee calculation for late days below or equal to 90
                BigDecimal interestFee = calculateAdditionalInterestFee(existingFee);
                existingFee.setInterestLateFee(existingFee.getInterestLateFee().add(interestFee));
                BigDecimal principalFee = calculateAdditionalPrincipalFee(existingFee);
                existingFee.setPrincipalLateFee(existingFee.getPrincipalLateFee().add(principalFee));
                existingFee.setTotalLateFee(existingFee.getTotalLateFee().add(interestFee).add(principalFee));

            }

            lateFeeCalculationRepo.save(existingFee);
        }
    }
    private BigDecimal calculateAdditionalInterestFee(HPLateFeeCalculation lateFee) {

        BigDecimal lateFeeBefore90Rate  = rateRepo.findValueByRateType("SME Late Fee Before 90 Days").divide(BigDecimal.valueOf(100));
        return lateFee.getHpRepaymentSchedule().getInterestODAmount()
                .multiply(lateFeeBefore90Rate)
                .setScale(2, RoundingMode.HALF_UP);
    }
    private BigDecimal calculateAdditionalPrincipalFee(HPLateFeeCalculation lateFee) {

        BigDecimal lateFeeBefore90Rate  = rateRepo.findValueByRateType("SME Late Fee Before 90 Days").divide(BigDecimal.valueOf(100));
        return lateFee.getHpRepaymentSchedule().getPrincipalODAmount()
                .multiply(lateFeeBefore90Rate)
                .setScale(2, RoundingMode.HALF_UP);
    }
    private BigDecimal calculateOutstandingAmount(HPLoan hpLoan) {
        // Fetch repayment schedules with required statuses
        List<HPSchedule> repaymentSchedules = repaymentScheduleRepo.findByHPLoanIdAndStatusIn(
                hpLoan.getId(), List.of(RepaymentStatus.NOT_DUE_YET, RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD, RepaymentStatus.INTEREST_OD_PRINCIPAL_OD));
        System.out.println("Repayment Schedules List : " + repaymentSchedules);
        if (repaymentSchedules.isEmpty()) {
            throw new RuntimeException("No repayment schedules found for the loan");
        }

        BigDecimal totalInterestOD = BigDecimal.ZERO;
        BigDecimal totalInstallment = BigDecimal.ZERO;
        BigDecimal totalPrincipalOD = BigDecimal.ZERO;

        BigDecimal outstandingAmount = BigDecimal.ZERO;

        for (HPSchedule schedule : repaymentSchedules) {
            BigDecimal interestAmount = schedule.getInterestAmount() != null ? schedule.getInterestAmount() : BigDecimal.ZERO;
            BigDecimal interestODAmount = schedule.getInterestODAmount() != null ? schedule.getInterestODAmount() : BigDecimal.ZERO;

            BigDecimal principalAmount = schedule.getPrincipal() != null ? schedule.getPrincipal() : BigDecimal.ZERO;
            BigDecimal principalODAmount = schedule.getPrincipalODAmount() != null ? schedule.getPrincipalODAmount() : BigDecimal.ZERO;

            if (schedule.getStatus() == RepaymentStatus.NOT_DUE_YET) {
                // Add the entire installment amount (principal + interest) to the outstanding amount
                BigDecimal installmentAmount = principalAmount.add(interestAmount);
                outstandingAmount = outstandingAmount.add(installmentAmount);

                // Add the entire installment (principal + interest) to the total installment
                totalInstallment = totalInstallment.add(installmentAmount);
            }

            if (schedule.getStatus() == RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD || schedule.getStatus() == RepaymentStatus.INTEREST_OD_PRINCIPAL_OD) {
                // Add the overdue interest and principal to the outstanding amount
                outstandingAmount = outstandingAmount.add(interestODAmount).add(principalODAmount);
                // Accumulate the total overdue interest and principal
                totalInterestOD = totalInterestOD.add(interestODAmount);
                totalPrincipalOD = totalPrincipalOD.add(principalODAmount);
            }
        }

        System.out.println("Total Interest OD Amount: " + totalInterestOD);
        System.out.println("Total Principal OD Amount: " + totalPrincipalOD);
        System.out.println("Total Remaining Installment: " + totalInstallment);
        System.out.println("OOOOOO "+outstandingAmount);
        return outstandingAmount;
    }
    private void recordLateFeeTracking(Integer hpLoanId, List<HPLateFeeCalculation> lateFees) {
        HPLateFeeTracking tracking = new HPLateFeeTracking();
        tracking.setHpLoan(hpLoanRepo.findById(hpLoanId)
                .orElseThrow(() -> new RuntimeException("HP Loan not found")));
        tracking.setTotalLateFees(calculateTotalLateFees(lateFees));
        tracking.setLateDays(lateFees.stream().mapToInt(HPLateFeeCalculation::getLateDays).max().orElse(0));
        tracking.setLateFeeRepaidDate(LocalDate.now());
        lateFeeTrackingRepo.save(tracking);
    }
    private void updateRepaymentSchedules(List<HPLateFeeCalculation> lateFees) {
        for (HPLateFeeCalculation lateFee : lateFees) {
            HPSchedule schedule = lateFee.getHpRepaymentSchedule();
            schedule.setLateFeeStatus(false);
            repaymentScheduleRepo.save(schedule);
        }
    }
    private HPLateFeeHolding createLateFeeHolding(Integer hpLoanId) {
        HPLateFeeHolding lateFeeHolding = new HPLateFeeHolding();
        lateFeeHolding.setHpLoan(hpLoanRepo.findById(hpLoanId)
                .orElseThrow(() -> new NotFoundException("HP Loan not found")));
        return lateFeeHolding;
    }
    private void updateAccountBalance(CurrentAccount account, BigDecimal amountToRepay) {
        account.setTotalBalence(account.getTotalBalence() - amountToRepay.doubleValue());
        accountRepo.save(account);
    }
    private BigDecimal calculateTotalLateFees(List<HPLateFeeCalculation> lateFees) {
        return lateFees.stream()
                .map(fee -> fee.getInterestLateFee().add(fee.getPrincipalLateFee())) // Corrected summation
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private CurrentAccount fetchCurrentAccountByHpLoanId(Integer hpLoanId) {
        return hpLoanRepo.findCurrentAccountByHpLoanId(hpLoanId)
                .orElseThrow(() -> new NotFoundException("Cannot Find Current Account By SME Loan ID"));
    }
    private BigDecimal getAvailableTransactionAmount(CurrentAccount account) {
        return transactionRepo.findByCurrentAccountIdAndDate(account, LocalDate.now())
                .stream()
                .map(transaction -> transaction.getType() == transactionType.CR
                        ? transaction.getAmount()
                        : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private HPLateFeeHolding fetchLateFeeHolding(Integer hpLoanId) {
        return lateFeeHoldingRepo.findByHpLoan_Id(hpLoanId).orElse(null);
    }
    private BigDecimal getHeldAmount(HPLateFeeHolding lateFeeHolding) {
        return lateFeeHolding != null ? lateFeeHolding.getHoldAmount() : BigDecimal.ZERO;
    }
}
