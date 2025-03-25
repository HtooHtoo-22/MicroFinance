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
public class SMEODRepayService {

    private final SMERepaymentScheduleRepo scheduleRepo;
    private final TransactionRepository transactionRepo;
    private final SMEODRepaymentTrackRepo repaymentTrackRepo;
    private final CurrentAccountRepository accountRepo;
    private final SMELateFeeCalculationRepo lateFeeRepo;

    @Autowired
    public SMEODRepayService(SMERepaymentScheduleRepo scheduleRepo,
                             TransactionRepository transactionRepo,
                             SMEODRepaymentTrackRepo repaymentTrackRepo,
                             CurrentAccountRepository accountRepo,
                             SMELateFeeCalculationRepo lateFeeRepo) {
        this.scheduleRepo = scheduleRepo;
        this.transactionRepo = transactionRepo;
        this.repaymentTrackRepo = repaymentTrackRepo;
        this.accountRepo = accountRepo;
        this.lateFeeRepo = lateFeeRepo;
    }

    @Transactional
    public void processODRepayment(BigDecimal transactionAmount, Integer smeLoanId) {
        logProcessStart(smeLoanId);

        List<SMERepaymentSchedule> overdueSchedules = findOverdueSchedules(smeLoanId);
        if (overdueSchedules.isEmpty()) {
            logNoOverdueSchedules(smeLoanId);
            return;
        }

        BigDecimal remainingAmount = processOverdueSchedules(overdueSchedules, transactionAmount);
        logRemainingAmount(remainingAmount);
    }

    private List<SMERepaymentSchedule> findOverdueSchedules(Integer smeLoanId) {
        return scheduleRepo.findBySmeLoanIdAndStatusIn(
                smeLoanId, List.of(RepaymentStatus.PARTIAL_OVERDUE, RepaymentStatus.FULL_OVERDUE));
    }

    private BigDecimal processOverdueSchedules(List<SMERepaymentSchedule> overdueSchedules, BigDecimal transactionAmount) {
        BigDecimal remainingAmount = transactionAmount;
        for (SMERepaymentSchedule schedule : overdueSchedules) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                applyLateFeeIfApplicable(schedule);
                sendRepaymentNotification(schedule, false);
                continue;
            }

            BigDecimal beforeRepayment = schedule.getInterestODAmount(); // Capture the amount before repayment
            remainingAmount = repaySchedule(schedule, remainingAmount);
            BigDecimal afterRepayment = schedule.getInterestODAmount(); // Capture the amount after repayment

            boolean isFullyPaid = afterRepayment.compareTo(BigDecimal.ZERO) == 0;
            sendRepaymentNotification(schedule, isFullyPaid);
        }
        return remainingAmount;
    }

    private BigDecimal repaySchedule(SMERepaymentSchedule schedule, BigDecimal remainingAmount) {
        BigDecimal amountToRepay = calculateAmountToRepay(schedule, remainingAmount);
        remainingAmount = remainingAmount.subtract(amountToRepay);

        updateRepaymentStatus(schedule, amountToRepay);
        logRepayment(schedule, amountToRepay);
        updateAccountBalance(schedule.getSmeLoan().getCurrentAccount(), amountToRepay);

        return remainingAmount;
    }
    private void sendRepaymentNotification(SMERepaymentSchedule schedule, boolean isFullyPaid) {
        String email = schedule.getSmeLoan().getCurrentAccount().getCif().getEmail();
        String phoneNumber = schedule.getSmeLoan().getCurrentAccount().getCif().getPhone();
        String subject;
        String message;

        if (isFullyPaid) {
            subject = "Overdue Payment Cleared";
            message = "Dear Customer,\n\nYour overdue payment for term "+schedule.getTermNumber()+" of Loan ID " + schedule.getSmeLoan().getLoanId() +
                    " has been successfully cleared. This term is now fully paid.\n\nThank you.";
        } else {

            BigDecimal lateFee = schedule.getInterestODAmount();
            subject = "Still Overdue Payment - Action Required";
            message = "Dear Customer,\n\nAlthough your previous late fee has been successfully repaid, " +
                    "your current overdue term "+schedule.getTermNumber()+" remains unpaid. As a result, late fees will restart. \n\n" +
                    "Please make the necessary payment as soon as possible to avoid additional charges.";
        }

        EmailSender.sendEmail(email, subject, message);
        SmsSender.sendSms(phoneNumber, message);
    }
    private BigDecimal calculateAmountToRepay(SMERepaymentSchedule schedule, BigDecimal remainingAmount) {
        return remainingAmount.min(schedule.getInterestODAmount());
    }

    private void updateRepaymentStatus(SMERepaymentSchedule schedule, BigDecimal amountToRepay) {
        BigDecimal remainingODAmount = schedule.getInterestODAmount().subtract(amountToRepay);
        schedule.setInterestODAmount(remainingODAmount);
        schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(amountToRepay));

        if (remainingODAmount.compareTo(BigDecimal.ZERO) == 0) {
            markScheduleAsPaid(schedule);
        } else {
            markScheduleAsPartialOverdue(schedule);
        }

        scheduleRepo.save(schedule);
    }

    private void markScheduleAsPaid(SMERepaymentSchedule schedule) {
        schedule.setStatus(RepaymentStatus.PAID);
        schedule.setFullyPaidDate(LocalDate.now());
    }

    private void markScheduleAsPartialOverdue(SMERepaymentSchedule schedule) {
        schedule.setStatus(RepaymentStatus.PARTIAL_OVERDUE);
        applyLateFee(schedule);
    }

    private void applyLateFeeIfApplicable(SMERepaymentSchedule schedule) {
        if (schedule.getInterestODAmount().compareTo(BigDecimal.ZERO) > 0) {
            applyLateFee(schedule);
        }
    }

    private void applyLateFee(SMERepaymentSchedule schedule) {
        logLateFeeApplication();
        schedule.setLateFeeStatus(true);
        scheduleRepo.save(schedule);

        SMELateFeeCalculation lateFee = createLateFee(schedule);
        lateFeeRepo.save(lateFee);
    }

    private SMELateFeeCalculation createLateFee(SMERepaymentSchedule schedule) {
        SMELateFeeCalculation lateFee = new SMELateFeeCalculation();
        lateFee.setSmeRepaymentSchedule(schedule);
        lateFee.setLateDays(1);
        lateFee.setLateFees(schedule.getInterestODAmount().multiply(BigDecimal.valueOf(0.001)));
        return lateFee;
    }

    private void logRepayment(SMERepaymentSchedule schedule, BigDecimal amountToRepay) {
        SMEODRepaymentTrack track = new SMEODRepaymentTrack();
        track.setSmeRepaymentSchedule(schedule);
        track.setPaid_od_amount(amountToRepay);
        track.setDate(LocalDateTime.now());
        if(schedule.getInterestODAmount()==BigDecimal.ZERO){
            track.setOdEndStatus(true);
        }
        repaymentTrackRepo.save(track);
    }

    private void updateAccountBalance(CurrentAccount account, BigDecimal amountToRepay) {
        account.setTotalBalence(account.getTotalBalence() - amountToRepay.doubleValue());
        accountRepo.save(account);
    }

    private void logProcessStart(Integer smeLoanId) {
        System.out.println("Processing OD Repayment for SME Loan ID: " + smeLoanId);
    }

    private void logNoOverdueSchedules(Integer smeLoanId) {
        System.out.println("No overdue schedules found for SME Loan ID: " + smeLoanId);
    }

    private void logRemainingAmount(BigDecimal remainingAmount) {
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("Excess transaction amount remaining: " + remainingAmount);
        }
    }

    private void logLateFeeApplication() {
        System.out.println("Applying Late Fee");
    }
}