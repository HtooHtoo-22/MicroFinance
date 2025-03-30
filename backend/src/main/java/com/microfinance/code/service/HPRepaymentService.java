package com.microfinance.code.service;



import com.microfinance.code.etc.EmailSender;
import com.microfinance.code.etc.SmsSender;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;


import com.microfinance.code.status.RepaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class HPRepaymentService {

    @Autowired
    private HPScheduleRepo hpScheduleRepository; // Repository for HPSchedule table

    @Autowired
    private HPLoanRepo hpLoanRepository; // Repository for HPLoan table

    @Autowired
    private HPLateFeeCalculationRepo lateFeeRepo; // Repository for Late Fee Calculation

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private HPRepaymentTrackRepo repaymentTrackRepo;

    @Transactional
    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE)
    public void processRepayments() {
        LocalDate today = LocalDate.now();
        boolean isHoliday = isHoliday(today);
        System.out.println("___________________________HP Auto Pay__________________________________________");

        // Process repayments for today
        processScheduledRepayments(today);

        System.out.println("______________________________________________________________________________");
    }
    private boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date); // Assuming holidayRepo has a method to check for holidays
    }
    @Transactional
    public void processScheduledRepayments(LocalDate today) {

        // Find all HP schedules that are due today or in the grace period
        List<HPSchedule> schedules = hpScheduleRepository.findByDueDateOrGracePeriodEndDateAndStatusIn(
                today,  List.of(RepaymentStatus.NOT_DUE_YET, RepaymentStatus.IN_GRACE_PERIOD)
        );

        if (schedules.isEmpty()) {
            System.out.println("No HP repayments due today.");
            return;
        }
        System.out.println(schedules);
        for (HPSchedule schedule : schedules) {
            processRepayment(schedule);
        }
    }

    private void processRepayment(HPSchedule schedule) {
        List<HPLateFeeCalculation> calculations = lateFeeRepo.findByHpLoanId(schedule.getHpLoan().getId());
        int maxLateDays = calculations.stream()
                .mapToInt(HPLateFeeCalculation::getLateDays)
                .max()
                .orElse(0);
        LocalDate today = LocalDate.now();
        if (schedule.getGracePeriodEndDate() != null && today.isBefore(schedule.getGracePeriodEndDate())) {
            schedule.setStatus(RepaymentStatus.IN_GRACE_PERIOD); // Mark as within grace period
            hpScheduleRepository.save(schedule);
            // Get the remaining grace period in days
            long daysLeft = ChronoUnit.DAYS.between(today, schedule.getGracePeriodEndDate());

            // Send SMS notification to the borrower
            String smsMessage = "RichCoin: Your HP loan Term: " + schedule.getTermNumber() + " is currently in grace period. " +
                    "Your grace period will end on " + schedule.getGracePeriodEndDate() + ". " +
                    "You have " + daysLeft + " days left to repay.";
            SmsSender.sendSms(schedule.getHpLoan().getCurrentAccount().getCif().getPhone(), smsMessage);

            // Send Email notification to the borrower
            String emailSubject = "HP Loan Grace Period Notification";
            String emailBody = "Dear " + schedule.getHpLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "Your HP loan Term: " + schedule.getTermNumber() + " is currently in grace period. " +
                    "Your grace period will end on " + schedule.getGracePeriodEndDate() + ". " +
                    "You have " + daysLeft + " days remaining to make your payment.\n\n" +
                    "Please ensure that your payment is made before the grace period ends to avoid penalties.\n\n" +
                    "Best regards,\n" +
                    "RichCoin Financial Services";
            EmailSender.sendEmail(schedule.getHpLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
            return;
        }
        // Get HP Loan and current account data
        // Get HP Loan and current account data
        HPLoan hpLoan = schedule.getHpLoan();
        BigDecimal totalBalance = BigDecimal.valueOf(hpLoan.getCurrentAccount().getTotalBalence());
        BigDecimal minBalance = BigDecimal.valueOf(hpLoan.getCurrentAccount().getMinAmount());
        BigDecimal dueInterest = schedule.getInterestAmount();
        BigDecimal duePrincipal = schedule.getPrincipal();


// Ensure minimum balance is preserved
        BigDecimal availableBalance = totalBalance.subtract(minBalance);
        BigDecimal totalRepaidAmount = BigDecimal.ZERO;

        BigDecimal installment = duePrincipal.add(dueInterest);

        // Deduct principal if balance allows
        if (availableBalance.compareTo(installment) >= 0) {

            availableBalance = availableBalance.subtract(installment);
            totalRepaidAmount = totalRepaidAmount.add(duePrincipal);
            schedule.setPrincipalODAmount(BigDecimal.ZERO);
            schedule.setStatus(RepaymentStatus.ALL_PAID);
            schedule.setFullyPaidDate(today);
            HPRepaymentTrack repaymentTrack = new HPRepaymentTrack();
            repaymentTrack.setHpSchedule(schedule);
            repaymentTrack.setDate(LocalDate.now());
            repaymentTrack.setPaidAmount(totalRepaidAmount);
            repaymentTrack.setRepayStatus(RepaymentStatus.ALL_PAID);
            repaymentTrackRepo.save(repaymentTrack);
            String smsMessage = "RichCoin: Your HP loan Term: " + schedule.getTermNumber() + " has been fully repaid. " +
                    "The full principal and interest amount of MMK " + duePrincipal + " has been cleared. " +
                    "Thank you for your timely payment!";
            SmsSender.sendSms(schedule.getHpLoan().getCurrentAccount().getCif().getPhone(), smsMessage);

// Full Repayment Email
            String emailSubject = "HP Loan Term: " + schedule.getTermNumber() + " - Full Repayment Confirmation";
            String emailBody = "Dear " + schedule.getHpLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "We are pleased to inform you that your HP loan Term: " + schedule.getTermNumber() + " has been fully repaid. " +
                    "The full principal and interest  amount of MMK " + duePrincipal + " has been cleared. Thank you for your timely payment.\n\n" +
                    "Best regards,\nRichCoin Financial Services";
            EmailSender.sendEmail(schedule.getHpLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
            hpLoan.getCurrentAccount().setTotalBalence(minBalance.add(availableBalance).doubleValue());

// Update schedule
            schedule.setInstallment(BigDecimal.ZERO);
            schedule.setInterestAmount(BigDecimal.ZERO);
            schedule.setPrincipal(BigDecimal.ZERO);
            schedule.setTotalRepaidAmount(installment);

// Save changes
            hpLoanRepository.save(hpLoan);
            hpScheduleRepository.save(schedule);
            return;
        }
//        } else if (availableBalance.compareTo(BigDecimal.ZERO) > 0) {
//            totalRepaidAmount = totalRepaidAmount.add(availableBalance);
//            schedule.setPrincipalODAmount(duePrincipal.subtract(availableBalance));
//            availableBalance = BigDecimal.ZERO;
//        } else {
//            schedule.setPrincipalODAmount(duePrincipal);
//        }


// Deduct interest first
        if (availableBalance.compareTo(dueInterest) >= 0) {
            System.out.println("Test");
            availableBalance = availableBalance.subtract(dueInterest);

            totalRepaidAmount = totalRepaidAmount.add(dueInterest);

            schedule.setInterestODAmount(BigDecimal.ZERO);
            schedule.setPrincipalODAmount(schedule.getPrincipal());
            schedule.setStatus(RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD);

            if (availableBalance.compareTo(BigDecimal.ZERO) > 0) {
                totalRepaidAmount = totalRepaidAmount.add(availableBalance);
                schedule.setPrincipalODAmount(duePrincipal.subtract(availableBalance));
                availableBalance = BigDecimal.ZERO;
            }

            HPRepaymentTrack repaymentTrack = new HPRepaymentTrack();
            repaymentTrack.setHpSchedule(schedule);
            repaymentTrack.setDate(LocalDate.now());
            repaymentTrack.setPaidAmount(dueInterest);
            repaymentTrack.setRepayStatus(RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD);
            repaymentTrackRepo.save(repaymentTrack);
            String smsMessage = "RichCoin: Your HP loan Term: " + schedule.getTermNumber() + " - Interest of MMK " +
                    dueInterest + " has been paid. However, your principal remains overdue. Please make the payment for the principal.";
            SmsSender.sendSms(schedule.getHpLoan().getCurrentAccount().getCif().getPhone(), smsMessage);

            String emailSubject = "HP Loan - Interest Paid, Principal Overdue";
            String emailBody = "Dear " + schedule.getHpLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "We would like to inform you that interest payment of MMK " + dueInterest + " has been successfully processed for your HP loan Term: " +
                    schedule.getTermNumber() + ". However, the principal remains overdue.\n\n" +
                    "Please ensure that you make the payment for the overdue principal to avoid further penalties.\n\n" +
                    "Best regards,\n" +
                    "RichCoin Financial Services";
            EmailSender.sendEmail(schedule.getHpLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
        } else if (availableBalance.compareTo(BigDecimal.ZERO) > 0) {

            totalRepaidAmount = totalRepaidAmount.add(availableBalance);
            schedule.setInterestODAmount(dueInterest.subtract(availableBalance));
            schedule.setPrincipalODAmount(schedule.getPrincipal());

            schedule.setStatus(RepaymentStatus.INTEREST_OD_PRINCIPAL_OD);

            HPRepaymentTrack repaymentTrack = new HPRepaymentTrack();
            repaymentTrack.setHpSchedule(schedule);
            repaymentTrack.setDate(LocalDate.now());
            repaymentTrack.setPaidAmount(totalRepaidAmount);
            repaymentTrack.setRepayStatus(RepaymentStatus.INTEREST_OD_PRINCIPAL_OD);
            repaymentTrackRepo.save(repaymentTrack);

            String smsMessage = "RichCoin: Your HP loan Term: " + schedule.getTermNumber() + " - Partial interest payment of MMK " +
                    availableBalance + " has been made. Your principal remains overdue, and the remaining interest balance is MMK " +
                    schedule.getInterestODAmount() + ". Please repay the remaining amount to avoid further penalties.";
            SmsSender.sendSms(schedule.getHpLoan().getCurrentAccount().getCif().getPhone(), smsMessage);

            String emailSubject = "HP Loan - Partial Interest Payment, Principal Overdue";
            String emailBody = "Dear " + schedule.getHpLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "We would like to inform you that a partial interest payment of MMK " + availableBalance + " has been made for your HP loan Term: " +
                    schedule.getTermNumber() + ". Your principal remains overdue, and the remaining interest balance is MMK " +
                    schedule.getInterestODAmount() + ".\n\n" +
                    "Please ensure that you repay the remaining amount to avoid further penalties.\n\n" +
                    "Best regards,\n" +
                    "RichCoin Financial Services";
            EmailSender.sendEmail(schedule.getHpLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
            availableBalance = BigDecimal.ZERO;
        } else {
            schedule.setInterestODAmount(dueInterest);
            schedule.setPrincipalODAmount(duePrincipal);
            schedule.setStatus(RepaymentStatus.INTEREST_OD_PRINCIPAL_OD);

            String smsMessage = "RichCoin: Your HP loan Term: " + schedule.getTermNumber() + " is now fully overdue. " +
                    "The full interest of MMK " + dueInterest + " and principal of MMK " + duePrincipal + " remain unpaid. " +
                    "Please make the payment as soon as possible to avoid further penalties.";
            SmsSender.sendSms(schedule.getHpLoan().getCurrentAccount().getCif().getPhone(), smsMessage);

            // Send email notification to the borrower regarding the overdue status
            String emailSubject = "HP Loan - Full Overdue Status";
            String emailBody = "Dear " + schedule.getHpLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "We would like to inform you that your HP loan Term: " + schedule.getTermNumber() + " is now fully overdue. " +
                    "The full interest of MMK " + dueInterest + " and principal of MMK " + duePrincipal + " remain unpaid.\n\n" +
                    "Please make the payment as soon as possible to avoid further penalties. If you need assistance, please contact us.\n\n" +
                    "Best regards,\n" +
                    "RichCoin Financial Services";
            EmailSender.sendEmail(schedule.getHpLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

        }



// Update account balance **after** processing both interest & principal
        BigDecimal newTotalBalance = minBalance.add(availableBalance);
        hpLoan.getCurrentAccount().setTotalBalence(newTotalBalance.doubleValue());

// Update schedule
        schedule.setInstallment(BigDecimal.ZERO);
        schedule.setInterestAmount(BigDecimal.ZERO);
        schedule.setPrincipal(BigDecimal.ZERO);
        schedule.setTotalRepaidAmount(totalRepaidAmount);

// Save changes
        hpLoanRepository.save(hpLoan);
        hpScheduleRepository.save(schedule);

        if(schedule.getGracePeriodEndDate()!=null && today.isEqual(schedule.getGracePeriodEndDate())
                && (schedule.getInterestODAmount()!=null || schedule.getPrincipalODAmount()!=null) && maxLateDays < 91){
            applyLateFee(schedule);
        }

    }

    private void applyLateFee(HPSchedule schedule) {
        System.out.println("Applying Late Fee");
        schedule.setLateFeeStatus(true); // Mark late fee status as true
        hpScheduleRepository.save(schedule);

        LocalDate dueDate = schedule.getDueDate();
        LocalDate graceEndDate = schedule.getGracePeriodEndDate();
        LocalDate currentDate = LocalDate.now();

        if (currentDate.equals(graceEndDate)) { // Late days start after grace period
            long lateDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);

            HPLateFeeCalculation lateFee = new HPLateFeeCalculation();
            lateFee.setHpRepaymentSchedule(schedule);
            lateFee.setLateDays((int) lateDays);

            // Calculate late fee based on overdue days
            BigDecimal interestLateFee = schedule.getInterestODAmount().multiply(BigDecimal.valueOf(0.001))
                    .multiply(BigDecimal.valueOf(lateDays));
            // Calculate Principal Late Fee (e.g., 0.1% of Principal OD Amount)
            BigDecimal principalLateFee = schedule.getPrincipalODAmount().multiply(BigDecimal.valueOf(0.001))
                    .multiply(BigDecimal.valueOf(lateDays));
            lateFee.setInterestLateFee(interestLateFee);
            lateFee.setPrincipalLateFee(principalLateFee);
            lateFee.setTotalLateFee(interestLateFee.add(principalLateFee));
            lateFeeRepo.save(lateFee);
        }
    }
}