package com.microfinance.code.service;

import com.microfinance.code.etc.EmailSender;
import com.microfinance.code.etc.SmsSender;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.status.RepaymentStatus;
import com.microfinance.code.status.transactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SMEODRepayService2 {

    private final SMERepaymentScheduleRepo scheduleRepo;
    private final TransactionRepository transactionRepo;
    private final SMEODRepaymentTrackRepo repaymentTrackRepo;
    private final CurrentAccountRepository accountRepo;
    private final SMELateFeeCalculationRepo lateFeeRepo;

    @Autowired
    public SMEODRepayService2(SMERepaymentScheduleRepo scheduleRepo,
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
    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void processODRepayment() {
        System.out.println("++++++++++++++++++++++++++Processing OD Repayment++++++++++++++++++++++++++++++");
        List<SMERepaymentSchedule> overdueSchedules = scheduleRepo.findByStatusInAndLateFeeStatus(
                List.of(RepaymentStatus.PARTIAL_OVERDUE, RepaymentStatus.FULL_OVERDUE), false);
        overdueSchedules.forEach(this::processRepaymentForSchedule);
        if ((overdueSchedules.isEmpty())){
            System.out.println("There is no OD Schedules With Late Day 0");
        }else {
            System.out.println("OD Schedules Which do not start late days : "+overdueSchedules);
        }

        System.out.println(("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"));
    }

    private void processRepaymentForSchedule(SMERepaymentSchedule schedule) {
        LocalDate today = LocalDate.now();
        List<SMELateFeeCalculation> calculations = lateFeeRepo.findBySmeLoanId(schedule.getSmeLoan().getId());
        int maxLateDays = calculations.stream()
                .mapToInt(SMELateFeeCalculation::getLateDays)
                .max()
                .orElse(0);

        CurrentAccount account = schedule.getSmeLoan().getCurrentAccount();
        BigDecimal availableFunds = calculateAvailableFunds(account);

        if (availableFunds.compareTo(BigDecimal.ZERO) <= 0) {
            if (maxLateDays<91){
                schedule.setLateFeeStatus(true);
                scheduleRepo.save(schedule);
                applyLateFee(schedule);

                String message = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() +
                        " is still overdue. Late fees will start accumulating from today (" + today + "). " +
                        "Please make your repayment as soon as possible to avoid additional penalties.";
                SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);

                String emailSubject = "SME Loan Overdue - Late Fee Started";
                String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "We would like to inform you that your SME loan Term: " + schedule.getTermNumber() +
                        " is still overdue as the due date has ended on " + schedule.getDueDate() + ".\n" +
                        "Late fees have started accumulating from today (" + today + ").\n\n" +
                        "Please make your repayment immediately to avoid further charges.\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";

                EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
            }
            return;
        }

        BigDecimal amountToRepay = availableFunds.min(schedule.getInterestODAmount());
        updateRepaymentStatus(schedule, amountToRepay);
        updateAccountBalance(account, amountToRepay);
        logRepayment(schedule, amountToRepay);

        if (schedule.getInterestODAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (maxLateDays<91){
                schedule.setLateFeeStatus(true);
                scheduleRepo.save(schedule);
                applyLateFee(schedule);
                // ===== SMS Message =====
                String smsMessage = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() +
                        " has received a partial repayment. Since full repayment was not made, late fees and late days are being applied from today (" + today + ")." +
                        " Please clear the remaining balance soon to avoid additional charges.";
                SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), smsMessage);
                // ===== Email Notification =====
                String emailSubject = "SME Loan Partial Repayment - Late Fee Applied";
                String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "We have received a partial repayment for your SME loan Term: " + schedule.getTermNumber() + ".\n" +
                        "However, the full repayment amount of " + schedule.getInterestODAmount().add(schedule.getTotalRepaidAmount()) + " was not covered. As a result, late days and late fees are being calculated starting from today (" + today + ").\n\n" +
                        "Amount Paid: " + schedule.getTotalRepaidAmount() + "\n" +
                        "Remaining Due: " + schedule.getInterestODAmount() + "\n\n" +
                        "Please repay the remaining amount as soon as possible to avoid further charges.\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";
                EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
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

    private void updateRepaymentStatus(SMERepaymentSchedule schedule, BigDecimal amountToRepay) {
        BigDecimal remainingAmount = schedule.getInterestODAmount().subtract(amountToRepay);
        schedule.setInterestODAmount(remainingAmount);
        schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(amountToRepay));

        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            schedule.setStatus(RepaymentStatus.PAID);
            schedule.setFullyPaidDate(LocalDate.now());
            // ===== SMS Message =====
            String smsMessage = "RichCoin: Thank you! Your SME loan Term: " + schedule.getTermNumber() +
                    " has been fully repaid successfully. We appreciate your timely repayment.";

            SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), smsMessage);

            // ===== Email Notification =====
            String emailSubject = "SME Loan Payment Successfully Completed";
            String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "We are pleased to inform you that your SME loan Term: " + schedule.getTermNumber() +
                    " has been fully repaid successfully.\n\n" +
                    "Thank you for your repayment. We appreciate your commitment.\n\n" +
                    "Best regards,\n" +
                    "RichCoin Financial Services";

            EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
        } else {
            schedule.setStatus(RepaymentStatus.PARTIAL_OVERDUE);
        }

        scheduleRepo.save(schedule);
    }

    private void updateAccountBalance(CurrentAccount account, BigDecimal amountToRepay) {
        account.setTotalBalence(account.getTotalBalence() - amountToRepay.doubleValue());
        accountRepo.save(account);
    }

    private void logRepayment(SMERepaymentSchedule schedule, BigDecimal amountToRepay) {
        SMEODRepaymentTrack track = new SMEODRepaymentTrack();
        track.setSmeRepaymentSchedule(schedule);
        track.setPaid_od_amount(amountToRepay);
        track.setDate(LocalDateTime.now());
        if(schedule.getStatus()==RepaymentStatus.PAID){
            track.setOdEndStatus(true);
        }
        repaymentTrackRepo.save(track);
    }

    private void applyLateFee(SMERepaymentSchedule schedule) {
        System.out.println("Applying Late Fee");
        schedule.setLateFeeStatus(true);
        scheduleRepo.save(schedule);

        SMELateFeeCalculation lateFee = new SMELateFeeCalculation();
        lateFee.setSmeRepaymentSchedule(schedule);
        lateFee.setLateDays(1);
        lateFee.setLateFees(schedule.getInterestODAmount().multiply(BigDecimal.valueOf(0.001)));
        lateFeeRepo.save(lateFee);
    }
}