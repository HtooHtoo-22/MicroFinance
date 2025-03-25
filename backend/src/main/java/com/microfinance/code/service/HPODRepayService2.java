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
public class HPODRepayService2 {
    private final HPScheduleRepo scheduleRepo;
    private final TransactionRepository transactionRepo;
    private final HPODRepaymentTrackRepo repaymentTrackRepo;
    private final CurrentAccountRepository accountRepo;
    private final HPLateFeeCalculationRepo lateFeeRepo;

    @Autowired
    public HPODRepayService2(HPScheduleRepo scheduleRepo,
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
    // @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void processODRepayment() {
        System.out.println("++++++++++++++++++++++++++Processing OD Repayment++++++++++++++++++++++++++++++");
        List<HPSchedule> overdueSchedules = scheduleRepo.findByStatusInAndLateFeeStatus(
                List.of(RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD, RepaymentStatus.INTEREST_OD_PRINCIPAL_OD), false);
        overdueSchedules.forEach(this::processRepaymentForSchedule);
        if ((overdueSchedules.isEmpty())){
            System.out.println("There is no OD Schedules With Late Day 0");
        }else {
            System.out.println("OD Schedules Which do not start late days : "+overdueSchedules);
        }

        System.out.println(("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"));
    }

    private void processRepaymentForSchedule(HPSchedule schedule) {
        LocalDate today = LocalDate.now();
        List<HPLateFeeCalculation> calculations = lateFeeRepo.findByHpLoanId(schedule.getHpLoan().getId());
        int maxLateDays = calculations.stream()
                .mapToInt(HPLateFeeCalculation::getLateDays)
                .max()
                .orElse(0);
        CurrentAccount account = schedule.getHpLoan().getCurrentAccount();
        BigDecimal availableFunds = calculateAvailableFunds(account);

        // If no available funds, return early
        if (availableFunds.compareTo(BigDecimal.ZERO) <= 0) {
            if (maxLateDays<91){
                schedule.setLateFeeStatus(true);
                scheduleRepo.save(schedule);
                applyLateFee(schedule);
                String message = "RichCoin: Your HP loan Term: " + schedule.getTermNumber() +
                        " is still overdue. Late fees will start accumulating from today (" + today + "). " +
                        "Please make your repayment as soon as possible to avoid additional penalties.";
                SmsSender.sendSms(schedule.getHpLoan().getCurrentAccount().getCif().getPhone(), message);

                String emailSubject = "HP Loan Overdue - Late Fee Started";
                String emailBody = "Dear " + schedule.getHpLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "We would like to inform you that your HP loan Term: " + schedule.getTermNumber() +
                        " is still overdue as the due date has ended on " + schedule.getDueDate() + ".\n" +
                        "Late fees have started accumulating from today (" + today + ").\n\n" +
                        "Please make your repayment immediately to avoid further charges.\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";

                EmailSender.sendEmail(schedule.getHpLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

            }
            return;
        }

        // Get due amounts
        BigDecimal dueInterest = schedule.getInterestODAmount();
        BigDecimal duePrincipal = schedule.getPrincipalODAmount();

        BigDecimal totalRepaid = BigDecimal.ZERO;

        // Step 1: Pay Interest First
        BigDecimal interestPaid = availableFunds.min(dueInterest);
        availableFunds = availableFunds.subtract(interestPaid);
        totalRepaid = totalRepaid.add(interestPaid);
        schedule.setInterestODAmount(dueInterest.subtract(interestPaid));

        // Step 2: Pay Principal Next (if funds remain)
        BigDecimal principalPaid = availableFunds.min(duePrincipal);
        availableFunds = availableFunds.subtract(principalPaid);
        totalRepaid = totalRepaid.add(principalPaid);
        schedule.setPrincipalODAmount(duePrincipal.subtract(principalPaid));

        // Step 3: Update Repayment Status
        updateRepaymentStatus(schedule, interestPaid, principalPaid);

        // Step 4: Update Account Balance
        updateAccountBalance(account, totalRepaid);

        // Step 5: Log Repayment
        logRepayment(schedule, interestPaid, principalPaid);
        if (schedule.getInterestODAmount().compareTo(BigDecimal.ZERO) > 0 || schedule.getPrincipalODAmount().compareTo(BigDecimal.ZERO)>0) {
            if (maxLateDays<91){
                schedule.setLateFeeStatus(true);
                scheduleRepo.save(schedule);
                applyLateFee(schedule);

                // ===== SMS Message =====
                String smsMessage = "RichCoin: Your HP loan Term: " + schedule.getTermNumber() +
                        " has received a partial repayment. Since full repayment was not made, late fees and late days are being applied from today (" + today + ")." +
                        " Please clear the remaining balance soon to avoid additional charges.";
                SmsSender.sendSms(schedule.getHpLoan().getCurrentAccount().getCif().getPhone(), smsMessage);

// ===== Email Notification =====
                String emailSubject = "HP Loan Partial Repayment - Late Fee Applied";
                String emailBody = "Dear " + schedule.getHpLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "We have received a partial repayment for your HP loan Term: " + schedule.getTermNumber() + ".\n" +
                        "However, the full repayment amount of " + schedule.getInterestODAmount().add(schedule.getPrincipalODAmount()).add(schedule.getTotalRepaidAmount()) + " was not covered. As a result, late days and late fees are being calculated starting from today (" + today + ").\n\n" +
                        "Amount Paid: " + schedule.getTotalRepaidAmount() + "\n" +
                        "Remaining Due (Interest): " + schedule.getInterestODAmount() + "\n" +
                        "Remaining Due (Principal): " + schedule.getPrincipalODAmount() + "\n\n" +
                        "Please repay the remaining amount as soon as possible to avoid further charges.\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";
                EmailSender.sendEmail(schedule.getHpLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

            }
        }
    }

    private void updateRepaymentStatus(HPSchedule schedule, BigDecimal interestPaid, BigDecimal principalPaid) {
        boolean interestCleared = schedule.getInterestODAmount().compareTo(BigDecimal.ZERO) == 0;
        boolean principalCleared = schedule.getPrincipalODAmount().compareTo(BigDecimal.ZERO) == 0;

        if (interestCleared && principalCleared) {
            schedule.setStatus(RepaymentStatus.ALL_PAID);
            // ===== SMS Message =====
            String smsMessage = "RichCoin: Thank you! Your HP loan Term: " + schedule.getTermNumber() +
                    " has been fully repaid for both interest and principal successfully. We appreciate your timely repayment.";

            SmsSender.sendSms(schedule.getHpLoan().getCurrentAccount().getCif().getPhone(), smsMessage);

// ===== Email Notification =====
            String emailSubject = "HP Loan Payment Successfully Completed";
            String emailBody = "Dear " + schedule.getHpLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "We are pleased to inform you that your HP loan Term: " + schedule.getTermNumber() +
                    " has been fully repaid for both interest and principal successfully.\n\n" +
                    "Thank you for your repayment. We appreciate your commitment.\n\n" +
                    "Best regards,\n" +
                    "RichCoin Financial Services";
            EmailSender.sendEmail(schedule.getHpLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

            schedule.setFullyPaidDate(LocalDate.now());
        } else if (interestCleared) {
            schedule.setStatus(RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD);
        } else {
            schedule.setStatus(RepaymentStatus.INTEREST_OD_PRINCIPAL_OD);
        }
        BigDecimal totalRepaid = interestPaid.add(principalPaid);
        schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(totalRepaid));
        scheduleRepo.save(schedule);
    }

    private void updateAccountBalance(CurrentAccount account, BigDecimal totalRepaid) {
        account.setTotalBalence(account.getTotalBalence() - totalRepaid.doubleValue());
        accountRepo.save(account);
    }
    private BigDecimal calculateAvailableFunds(CurrentAccount account) {
        return transactionRepo.findByCurrentAccountIdAndDate(account, LocalDate.now())
                .stream()
                .map(transaction -> transaction.getType() == transactionType.CR
                        ? transaction.getAmount()
                        : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private void logRepayment(HPSchedule schedule, BigDecimal interestRepaid, BigDecimal principalRepaid) {
        HPODRepaymentTrack track = new HPODRepaymentTrack();
        track.setHpRepaymentSchedule(schedule);
        track.setPaidInterestODAmount(interestRepaid);  // Track interest OD repayment
        track.setPaidPrincipalODAmount(principalRepaid);  // Track principal OD repayment
        track.setDate(LocalDateTime.now());
        track.setRepayStatus(schedule.getStatus());
        repaymentTrackRepo.save(track);
    }



    private void applyLateFee(HPSchedule schedule) {
        System.out.println("Applying Late Fee");
        schedule.setLateFeeStatus(true);
        scheduleRepo.save(schedule);

        HPLateFeeCalculation lateFee = new HPLateFeeCalculation();
        lateFee.setHpRepaymentSchedule(schedule);

        // Calculate Interest Late Fee (e.g., 0.1% of Interest OD Amount)
        BigDecimal interestLateFee = schedule.getInterestODAmount().multiply(BigDecimal.valueOf(0.001));

        // Calculate Principal Late Fee (e.g., 0.1% of Principal OD Amount)
        BigDecimal principalLateFee = schedule.getPrincipalODAmount().multiply(BigDecimal.valueOf(0.001));

        lateFee.setLateDays(1);
        lateFee.setInterestLateFee(interestLateFee);
        lateFee.setPrincipalLateFee(principalLateFee);
        lateFee.setTotalLateFee(interestLateFee.add(principalLateFee));
        lateFeeRepo.save(lateFee);
    }

}
