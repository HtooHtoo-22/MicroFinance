package com.microfinance.code.service;

import com.microfinance.code.etc.EmailSender;
import com.microfinance.code.etc.SmsSender;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.status.RepaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HPODRepayService {
    private final HPScheduleRepo scheduleRepo;
    private final TransactionRepository transactionRepo;
    private final HPODRepaymentTrackRepo repaymentTrackRepo;
    private final CurrentAccountRepository accountRepo;
    private final HPLateFeeCalculationRepo lateFeeRepo;
    @Autowired
    public HPODRepayService(HPScheduleRepo scheduleRepo,
                            TransactionRepository transactionRepo,
                            HPODRepaymentTrackRepo repaymentTrackRepo,
                            CurrentAccountRepository accountRepo,
                            HPLateFeeCalculationRepo lateFeeRepo) {
        this.scheduleRepo = scheduleRepo;
        this.transactionRepo = transactionRepo;
        this.repaymentTrackRepo = repaymentTrackRepo;
        this.accountRepo = accountRepo;
        this.lateFeeRepo = lateFeeRepo;
    }
    @Transactional
    public void processODRepayment(BigDecimal transactionAmount, Integer hpLoanId) {
        System.out.println(hpLoanId);
        System.out.println("Remaining Amount For OD Repay : "+transactionAmount);
        List<HPSchedule> overdueSchedules = findOverdueSchedules(hpLoanId);
        if (overdueSchedules.isEmpty()) {
            // logNoOverdueSchedules(hpLoanId);
            return;
        }

        BigDecimal remainingAmount = processOverdueInterest(overdueSchedules, transactionAmount);
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            remainingAmount = processOverduePrincipal(overdueSchedules, remainingAmount);
        }

        //logRemainingAmount(remainingAmount);
    }

    private BigDecimal processOverdueInterest(List<HPSchedule> overdueSchedules, BigDecimal transactionAmount) {
        BigDecimal remainingAmount = transactionAmount;
        for (HPSchedule schedule : overdueSchedules) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                applyLateFeeIfApplicable(schedule);
                continue;
            }

            remainingAmount = repayInterestOD(schedule, remainingAmount);

            applyLateFeeIfApplicable(schedule);

        }
        return remainingAmount;
    }

    private BigDecimal processOverduePrincipal(List<HPSchedule> overdueSchedules, BigDecimal transactionAmount) {
        BigDecimal remainingAmount = transactionAmount;
        for (HPSchedule schedule : overdueSchedules) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                applyLateFeeIfApplicable(schedule);
                continue;
            }

            remainingAmount = repayPrincipalOD(schedule, remainingAmount);
            applyLateFeeIfApplicable(schedule);

        }

        return remainingAmount;
    }

    private BigDecimal repayInterestOD(HPSchedule schedule, BigDecimal remainingAmount) {
        BigDecimal amountToRepay = calculateAmountToRepay(schedule.getInterestODAmount(), remainingAmount);
        remainingAmount = remainingAmount.subtract(amountToRepay);
        System.out.println("AMount to Repay : "+amountToRepay);
        System.out.println("REEEMain AMount : "+remainingAmount);
        updateRepaymentStatusForInterest(schedule, amountToRepay);
        //logRepayment(schedule, amountToRepay);
        updateAccountBalance(schedule.getHpLoan().getCurrentAccount(), amountToRepay);
        logRepayment(schedule, amountToRepay, BigDecimal.ZERO);

        // Fetch HP Loan ID and term number
        String hpLoanId = schedule.getHpLoan().getLoanId();  // Assuming getId() fetches the HP Loan ID
        int termNumber = schedule.getTermNumber(); // Assuming getTermNumber() fetches the term number

        // Send email notification
        String email = schedule.getHpLoan().getCurrentAccount().getCif().getEmail();
        String emailSubject = "Interest OD Payment - HP Loan " + hpLoanId + " (Term " + termNumber + ")";
        String emailMessage = "Dear customer, your interest OD amount of " + amountToRepay +
                " has been successfully paid. The interest overdue is now clear.";
        EmailSender.sendEmail(email, emailSubject, emailMessage);

        // Send SMS notification
        String phoneNumber = schedule.getHpLoan().getCurrentAccount().getCif().getPhone();
        String smsMessage = "Interest OD of " + amountToRepay + " paid for HP Loan " + hpLoanId +
                " (Term " + termNumber + "). The interest overdue is now clear.";
        SmsSender.sendSms(phoneNumber, smsMessage);

        return remainingAmount;
    }

    private BigDecimal repayPrincipalOD(HPSchedule schedule, BigDecimal remainingAmount) {
        BigDecimal amountToRepay = calculateAmountToRepay(schedule.getPrincipalODAmount(), remainingAmount);
        remainingAmount = remainingAmount.subtract(amountToRepay);

        updateRepaymentStatusForPrincipal(schedule, amountToRepay);
        //logRepayment(schedule, amountToRepay);
        updateAccountBalance(schedule.getHpLoan().getCurrentAccount(), amountToRepay);
        logRepayment(schedule, BigDecimal.ZERO, amountToRepay);

        // Fetch HP Loan ID and term number
        String hpLoanId = schedule.getHpLoan().getLoanId();  // Assuming getId() fetches the HP Loan ID
        int termNumber = schedule.getTermNumber(); // Assuming getTermNumber() fetches the term number

        // Send email notification
        String email = schedule.getHpLoan().getCurrentAccount().getCif().getEmail();
        String emailSubject = "Term Payment Completed - HP Loan " + hpLoanId + " (Term " + termNumber + ")";
        String emailMessage = "Dear customer, your interest OD and principal OD amount of " + amountToRepay +
                " has been successfully paid. Your term " + termNumber + " is now fully paid.";
        EmailSender.sendEmail(email, emailSubject, emailMessage);

        // Send SMS notification
        String phoneNumber = schedule.getHpLoan().getCurrentAccount().getCif().getPhone();
        String smsMessage = "HP Loan " + hpLoanId + " (Term " + termNumber + ") fully paid. Thank you.";
        SmsSender.sendSms(phoneNumber, smsMessage);

        return remainingAmount;
    }

    private void updateRepaymentStatusForInterest(HPSchedule schedule, BigDecimal amountToRepay) {
        BigDecimal remainingODAmount = schedule.getInterestODAmount().subtract(amountToRepay);
        System.out.println("A Mount To "+ amountToRepay);
        System.out.println("Remaining OD AM : "+remainingODAmount);
        schedule.setInterestODAmount(remainingODAmount);
        schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(amountToRepay));

        if (remainingODAmount.compareTo(BigDecimal.ZERO) == 0) {
            markScheduleAsInterestPaid(schedule); // Interest fully paid
        }

        scheduleRepo.save(schedule);
    }

    private void updateRepaymentStatusForPrincipal(HPSchedule schedule, BigDecimal amountToRepay) {
        BigDecimal remainingODAmount = schedule.getPrincipalODAmount().subtract(amountToRepay);
        schedule.setPrincipalODAmount(remainingODAmount);
        schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(amountToRepay));

        if (remainingODAmount.compareTo(BigDecimal.ZERO) == 0) {
            markScheduleAsPrincipalPaid(schedule);
        }

        scheduleRepo.save(schedule);
    }

    private void markScheduleAsInterestPaid(HPSchedule schedule) {
        if (schedule.getPrincipalODAmount().compareTo(BigDecimal.ZERO) == 0) {
            // If principal is also paid, mark as ALL_PAID
            schedule.setStatus(RepaymentStatus.ALL_PAID);
            schedule.setFullyPaidDate(LocalDate.now());
        } else {
            // If principal is still overdue, mark as INTEREST_PAID_PRINCIPAL_OD
            schedule.setStatus(RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD);
        }
    }

    private void markScheduleAsPrincipalPaid(HPSchedule schedule) {
        // Since principal is paid, interest must already be paid
        schedule.setStatus(RepaymentStatus.ALL_PAID);
        schedule.setFullyPaidDate(LocalDate.now());
        schedule.setLateFeeStatus(false);
        scheduleRepo.save(schedule);
        lateFeeRepo.findByHpRepaymentSchedule(schedule).ifPresent(lateFeeRepo::delete);
    }

    private BigDecimal calculateAmountToRepay(BigDecimal odAmount, BigDecimal remainingAmount) {
        System.out.println("Amount to repay : "+remainingAmount.min(odAmount));
        return remainingAmount.min(odAmount);
    }
    private List<HPSchedule> findOverdueSchedules(Integer hpLoanId) {
        return scheduleRepo.findByHPLoanIdAndStatusIn(
                hpLoanId, List.of(RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD, RepaymentStatus.INTEREST_OD_PRINCIPAL_OD));
    }
    private void updateAccountBalance(CurrentAccount account, BigDecimal amountToRepay) {
        account.setTotalBalence(account.getTotalBalence() - amountToRepay.doubleValue());
        accountRepo.save(account);
    }



    private void applyLateFeeIfApplicable(HPSchedule schedule) {
        // Apply late fee if either interest OD or principal OD is greater than 0
        if (schedule.getInterestODAmount().compareTo(BigDecimal.ZERO) > 0 ||
                schedule.getPrincipalODAmount().compareTo(BigDecimal.ZERO) > 0) {
            applyLateFee(schedule);
        }
    }
    private void applyLateFee(HPSchedule schedule) {
        System.out.println("Applying Late Fee");
        schedule.setLateFeeStatus(true);
        scheduleRepo.save(schedule);

        // Check if a late fee row already exists for this schedule
        HPLateFeeCalculation lateFee = lateFeeRepo.findByHpRepaymentSchedule(schedule)
                .orElse(new HPLateFeeCalculation());

        // Set the schedule for the late fee
        lateFee.setHpRepaymentSchedule(schedule);

        // Calculate Interest Late Fee (e.g., 0.1% of Interest OD Amount)
        BigDecimal interestLateFee = schedule.getInterestODAmount().multiply(BigDecimal.valueOf(0.001));

        // Calculate Principal Late Fee (e.g., 0.1% of Principal OD Amount)
        BigDecimal principalLateFee = schedule.getPrincipalODAmount().multiply(BigDecimal.valueOf(0.001));

        // Set late fee details
        lateFee.setLateDays(1);
        lateFee.setInterestLateFee(interestLateFee);
        lateFee.setPrincipalLateFee(principalLateFee);
        lateFee.setTotalLateFee(interestLateFee.add(principalLateFee));

        // Save or update the late fee row
        lateFeeRepo.save(lateFee);

        // If both interest OD and principal OD are fully paid, delete the late fee row
        if (schedule.getInterestODAmount().compareTo(BigDecimal.ZERO) == 0 &&
                schedule.getPrincipalODAmount().compareTo(BigDecimal.ZERO) == 0) {
            schedule.setLateFeeStatus(false);
            scheduleRepo.save(schedule);
            lateFeeRepo.delete(lateFee);
        }
    }
    private void logRepayment(HPSchedule schedule, BigDecimal paidInterestODAmount, BigDecimal paidPrincipalODAmount) {
        HPODRepaymentTrack track = new HPODRepaymentTrack();
        track.setHpRepaymentSchedule(schedule);
        track.setPaidInterestODAmount(paidInterestODAmount); // Track repaid interest OD
        track.setPaidPrincipalODAmount(paidPrincipalODAmount); // Track repaid principal OD
        track.setDate(LocalDateTime.now());
        track.setRepayStatus(schedule.getStatus());
        repaymentTrackRepo.save(track);
    }
}