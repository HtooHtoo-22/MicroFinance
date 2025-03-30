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
public class SMERepaymentService {
    @Autowired
    private HolidayRepository holidayRepository; // Repository for Holiday table

    @Autowired
    private SMERepaymentScheduleRepo repaymentScheduleRepository; // Repository for SMERepaymentSchedule table

    @Autowired
    private CurrentAccountRepository currentAccountRepository; // Repository for CurrentAccount table
    @Autowired
    private  SMERepaymentScheduleRepo scheduleRepo;
    @Autowired
    private  SMELateFeeCalculationRepo lateFeeRepo;
    @Autowired
    private SMERepaymentTrackRepo repaymentTrackRepo;
    @Autowired
    private SMELoanHasCollateralRepo loanHasCollateralRepo;
    @Autowired
    private CollateralRepo collateralRepo;
    @Autowired
    private SMELoanRepo loanRepo;
    @Autowired
    private RateRepository rateRepo;
    @Transactional
 //   @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE)
    public void processRepayments() {
        LocalDate today = LocalDate.now();

        // Check if today is a holiday
        boolean isHoliday = isHoliday(today);
//        if (isHoliday) {
//            System.out.println("Today is a holiday. Skipping repayments.");
//            return;
//        }
        System.out.println("___________________________Auto Pay__________________________________________");
        // Proceed if it's not a holiday
        processScheduledRepayments(today);
        System.out.println("______________________________________________________________________________");
    }
    private boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date); // Assuming holidayRepo has a method to check for holidays
    }
    @Transactional
    public void processScheduledRepayments(LocalDate today) {
        List<SMERepaymentSchedule> schedules = repaymentScheduleRepository.findByDueDateOrGracePeriodEndDateAndStatusIn(today,today,List.of(RepaymentStatus.NOT_DUE_YET,RepaymentStatus.IN_GRACE_PERIOD));

        if (schedules.isEmpty()) {
            System.out.println("No repayments due today.");
            return;
        }

        for (SMERepaymentSchedule schedule : schedules) {
            processRepayment(schedule);
        }
    }

    private void processRepayment(SMERepaymentSchedule schedule) {

        List<SMELateFeeCalculation> calculations = lateFeeRepo.findBySmeLoanId(schedule.getSmeLoan().getId());
        int maxLateDays = calculations.stream()
                .mapToInt(SMELateFeeCalculation::getLateDays)
                .max()
                .orElse(0);

        // Get the current account and total balance as BigDecimal
        CurrentAccount currentAccount = schedule.getSmeLoan().getCurrentAccount();
        BigDecimal totalBalance = BigDecimal.valueOf(currentAccount.getTotalBalence()); // Convert to BigDecimal
        BigDecimal dueAmount = schedule.getInterestAmount(); // Assume interestAmount is already BigDecimal
        LocalDate today = LocalDate.now();

        // Ensure the minimum balance is preserved
        BigDecimal availableBalance = totalBalance.subtract(BigDecimal.valueOf(currentAccount.getMinAmount()));

        // Check if today is still within the grace period
        if (schedule.getGracePeriodEndDate() != null && today.isBefore(schedule.getGracePeriodEndDate())) {
            schedule.setStatus(RepaymentStatus.IN_GRACE_PERIOD); // Mark as within grace period

            String message = "RichCoin: Your SME loan Term :"+schedule.getTermNumber()+" is currently in grace period. " +
                    "Your grace period will end on " + schedule.getGracePeriodEndDate() +
                    ". You have " + ChronoUnit.DAYS.between(today, schedule.getGracePeriodEndDate()) +
                    " days left to repay.";
            SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);

            String emailSubject = "SME Loan Grace Period Notification";
            String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "We would like to inform you that your SME loan Term: "+schedule.getTermNumber()+" is currently in the grace period. " +
                    "Your grace period will end on " + schedule.getGracePeriodEndDate() + ". " +
                    "You have " + ChronoUnit.DAYS.between(today, schedule.getGracePeriodEndDate()) +
                    " days remaining to make your payment.\n\n" +
                    "Please ensure that your payment is made before the grace period ends to avoid penalties.\n\n" +
                    "Best regards,\n" +
                    "RichCoin Financial Services";
            EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

        } else if (availableBalance.compareTo(dueAmount) >= 0) {
            // Enough balance to repay
            currentAccount.setTotalBalence(totalBalance.subtract(dueAmount).doubleValue());
            schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(dueAmount));
            schedule.setFullyPaidDate(today);

            // Check if this is the FINAL TERM (principal is due)
            if (schedule.getTermNumber() == schedule.getSmeLoan().getDuration()) {
                // Final term: Interest is paid, now check principal
                BigDecimal principalDue = schedule.getSmeLoan().getPrincipal();
                if (availableBalance.compareTo(principalDue) >= 0) {
                    // Principal is paid → Close loan
                    currentAccount.setTotalBalence(totalBalance.subtract(principalDue).doubleValue());
                    schedule.setStatus(RepaymentStatus.PAID);
                    System.out.println("Principal Enough");
                    List<SMELoanHasCollateral> toDelete = loanHasCollateralRepo.findBySmeLoan(schedule.getSmeLoan());
                    // 2. Delete in batch (efficient)
                    loanHasCollateralRepo.deleteAll(toDelete); // Single DB operation
                    String message = "RichCoin: Congratulations! Your SME loan Term: " + schedule.getTermNumber() +
                            " has been fully repaid, including the principal amount of MMK " + principalDue +
                            ". Your collateral has now been released. Thank you for your trust in us.";
                    SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);
                    String emailSubject = "SME Loan Term: " + schedule.getTermNumber() + " - Loan Fully Repaid";
                    String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                            "Congratulations! You have successfully repaid the final term of your SME loan, including the principal amount of MMK " + principalDue + ".\n" +
                            "As a result, your collateral for this loan has now been released. We appreciate your commitment to timely repayments.\n\n" +
                            "Thank you for choosing RichCoin Financial Services.\n\n" +
                            "Best regards,\n" +
                            "RichCoin Financial Services";
                    EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

                } else {
                    System.out.println("Principal Not Enough");
                    // Principal unpaid → DEFAULT
                    schedule.setStatus(RepaymentStatus.PAID); // Interest is paid
                    SMELoan loan = schedule.getSmeLoan();
                    loan.setStatus(com.microfinance.code.status.LoanStatus.DEFAULT);
                    loanRepo.save(loan);
                    //   schedule.getSmeLoan().setStatus(com.microfinance.code.status.LoanStatus.DEFAULT);
                    // Fetch all collaterals in one query
                    List<Collateral> collaterals = loanHasCollateralRepo.findCollateralsBySmeLoanId(schedule.getSmeLoan().getId());

                    // Update all collaterals in memory (no DB hits yet)
                    collaterals.forEach(collateral -> collateral.setHeldByCompany(true)); // Or setHeldByCompany if you prefer

                    // Batch save (1 transaction)
                    collateralRepo.saveAll(collaterals); // More efficient than individual saves
                    String message = "RichCoin: Your loan's principal amount of MMK " + principalDue +
                            " has not been repaid. Your collateral will remain with us until full payment is made.";
                    SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);
                    String emailSubject = "Outstanding Principal Payment";
                    String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                            "Your loan's principal amount of MMK " + principalDue + " has not been repaid.\n" +
                            "As a result, your collateral will remain with us until the full payment is made.\n\n" +
                            "Please arrange the payment as soon as possible. If you need assistance, feel free to contact us.\n\n" +
                            "Best regards,\n" +
                            "RichCoin Financial Services";
                    EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

                }
            } else {
                // Regular term (only interest)
                schedule.setStatus(RepaymentStatus.PAID);
            }
            SMERepaymentTrack repaymentTrack = new SMERepaymentTrack();
            repaymentTrack.setSmeRepaymentSchedule(schedule);
            repaymentTrack.setDate(today);
            repaymentTrack.setPaidAmount(dueAmount);
            repaymentTrackRepo.save(repaymentTrack);


            String message = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() + " has been successfully paid. " +
                    "You repaid MMK " + dueAmount + ". " +
                    "Your current balance is MMK " + currentAccount.getTotalBalence() + ". " +
                    "Thank you for your timely repayment.";
            SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);

            String emailSubject = "SME Loan Term: " + schedule.getTermNumber() + " - Repayment Successful";
            String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                    "We are pleased to inform you that your SME loan Term: " + schedule.getTermNumber() + " has been successfully paid. " +
                    "You have repaid MMK " + dueAmount + " today. Your current balance is MMK " + currentAccount.getTotalBalence() + ".\n\n" +
                    "Thank you for your timely repayment.\n\n" +
                    "Best regards,\n" +
                    "RichCoin Financial Services";
            EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

        } else if (availableBalance.compareTo(BigDecimal.ZERO) > 0) {
            // Not enough balance, but partial payment can be made (without touching the minimum)
            currentAccount.setTotalBalence(currentAccount.getMinAmount()); // Keep the minimum balance intact
            schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(availableBalance));
            schedule.setInterestODAmount(schedule.getInterestODAmount().add(dueAmount.subtract(availableBalance))); // Remaining amount as OD interest
            schedule.setStatus(RepaymentStatus.PARTIAL_OVERDUE);
            if(schedule.getGracePeriodEndDate()!=null && maxLateDays<91){
                BigDecimal lateFees = applyLateFee(schedule);
                String message = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() + " is currently in partial overdue status. " +
                        "You have repaid MMK " + availableBalance + ". Your grace period ended on " + schedule.getGracePeriodEndDate() +
                        ". You have " + maxLateDays + " late days. " +
                        "A late fee of MMK " + lateFees + " has been applied to your account. " +
                        "Please ensure the remaining balance is repaid to avoid further penalties.";
                SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);
                String emailSubject = "SME Loan Term: " + schedule.getTermNumber() + " - Partial Repayment and Late Fee Notification";
                String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "Your SME loan Term: " + schedule.getTermNumber() + " is currently in partial overdue status. " +
                        "You have repaid MMK " + availableBalance + ". Your grace period ended on " + schedule.getGracePeriodEndDate() +
                        ". You have " + maxLateDays + " late days.\n" +
                        "A late fee of MMK " + lateFees + " has been applied to your account.\n\n" +
                        "Please make the remaining payment to avoid further penalties. Your current balance is MMK " +
                        currentAccount.getTotalBalence() + ".\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";
                EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

            }else{
                String message = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() + " is in partial overdue status. " +
                        "You have repaid MMK " + availableBalance + ". Your current balance is MMK " + currentAccount.getTotalBalence() + "." +
                        " Please make the remaining payment to avoid further penalties.";
                SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);
                String emailSubject = "SME Loan Term: " + schedule.getTermNumber() + " - Partial Repayment Reminder";
                String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "Your SME loan Term: " + schedule.getTermNumber() + " is in partial overdue status. " +
                        "You have repaid MMK " + availableBalance + ". Your current balance is MMK " + currentAccount.getTotalBalence() + ".\n\n" +
                        "Please make the remaining payment as soon as possible to avoid further penalties.\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";
                EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);

            }
            schedule.setInterestAmount(new BigDecimal(0.0));
            SMERepaymentTrack repaymentTrack = new SMERepaymentTrack();
            repaymentTrack.setSmeRepaymentSchedule(schedule);
            repaymentTrack.setDate(today);
            repaymentTrack.setPaidAmount(availableBalance);
            repaymentTrack.setOdStatus(true);
            repaymentTrackRepo.save(repaymentTrack);
            if (schedule.getTermNumber() == schedule.getSmeLoan().getDuration()) {
                schedule.getSmeLoan().setStatus(com.microfinance.code.status.LoanStatus.DEFAULT);
                // Fetch all collaterals in one query
                List<Collateral> collaterals = loanHasCollateralRepo.findCollateralsBySmeLoanId(schedule.getSmeLoan().getId());

                // Update all collaterals in memory (no DB hits yet)
                collaterals.forEach(collateral -> collateral.setHeldByCompany(true)); // Or setHeldByCompany if you prefer

                // Batch save (1 transaction)
                collateralRepo.saveAll(collaterals); // More efficient than individual saves
                String message = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() + " has been successfully paid. " +
                        "You repaid MMK " + dueAmount + ". " +
                        "Your current balance is MMK " + currentAccount.getTotalBalence() + ". " +
                        "Thank you for your timely repayment.";
                SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);

                String emailSubject = "SME Loan Term: " + schedule.getTermNumber() + " - Repayment Successful";
                String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "We are pleased to inform you that your SME loan Term: " + schedule.getTermNumber() + " has been successfully paid. " +
                        "You have repaid MMK " + dueAmount + " today. Your current balance is MMK " + currentAccount.getTotalBalence() + ".\n\n" +
                        "Thank you for your timely repayment.\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";
                EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
            }
        } else {
            // No available balance, full overdue
            schedule.setInterestODAmount(schedule.getInterestODAmount().add(dueAmount)); // All remaining amount is OD interest
            schedule.setStatus(RepaymentStatus.FULL_OVERDUE);
            if(schedule.getGracePeriodEndDate()!=null && maxLateDays<91){
                BigDecimal lateFees = applyLateFee(schedule);
                // SMS Notification
                String message = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() + " is now in full overdue status. " +
                        "Your interest amount of MMK " + dueAmount + " has been transferred to overdue interest. " +
                        "You are now in default, and a late fee of MMK " + lateFees + " has been applied. " +
                        "Please make the full payment as soon as possible to avoid further penalties.";
                SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);

                // Email Notification
                String emailSubject = "SME Loan Term: " + schedule.getTermNumber() + " - Full Overdue Notification";
                String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "Your SME loan Term: " + schedule.getTermNumber() + " is now in full overdue status. " +
                        "Your interest amount of MMK " + dueAmount + " has been transferred to overdue interest. " +
                        "You are currently in default, and a late fee of MMK " + lateFees + " has been applied to your account.\n\n" +
                        "Please make the full payment as soon as possible to avoid further penalties. Your current balance is MMK " +
                        currentAccount.getTotalBalence() + ".\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";
                EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
            }else{
                String message = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() + " is now in full overdue status. " +
                        "Your outstanding amount of MMK " + dueAmount + " has been transferred to overdue interest. " +
                        "Please make the full payment as soon as possible to avoid further penalties.";
                SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);

                // Email Notification
                String emailSubject = "SME Loan Term: " + schedule.getTermNumber() + " - Full Overdue Reminder";
                String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "Your SME loan Term: " + schedule.getTermNumber() + " is now in full overdue status. " +
                        "Your outstanding amount of MMK " + dueAmount + " has been transferred to overdue interest. " +
                        "Please make the full payment as soon as possible to avoid further penalties. Your current balance is MMK " +
                        currentAccount.getTotalBalence() + ".\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";
                EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
            }
            schedule.setInterestAmount(new BigDecimal(0.0));
            if (schedule.getTermNumber() == schedule.getSmeLoan().getDuration()) {
                schedule.getSmeLoan().setStatus(com.microfinance.code.status.LoanStatus.DEFAULT);
                // Fetch all collaterals in one query
                List<Collateral> collaterals = loanHasCollateralRepo.findCollateralsBySmeLoanId(schedule.getSmeLoan().getId());

                // Update all collaterals in memory (no DB hits yet)
                collaterals.forEach(collateral -> collateral.setHeldByCompany(true)); // Or setHeldByCompany if you prefer

                // Batch save (1 transaction)
                collateralRepo.saveAll(collaterals); // More efficient than individual saves
                String message = "RichCoin: Your SME loan Term: " + schedule.getTermNumber() + " has been successfully paid. " +
                        "You repaid MMK " + dueAmount + ". " +
                        "Your current balance is MMK " + currentAccount.getTotalBalence() + ". " +
                        "Thank you for your timely repayment.";
                SmsSender.sendSms(schedule.getSmeLoan().getCurrentAccount().getCif().getPhone(), message);

                String emailSubject = "SME Loan Term: " + schedule.getTermNumber() + " - Repayment Successful";
                String emailBody = "Dear " + schedule.getSmeLoan().getCurrentAccount().getCif().getUserName() + ",\n\n" +
                        "We are pleased to inform you that your SME loan Term: " + schedule.getTermNumber() + " has been successfully paid. " +
                        "You have repaid MMK " + dueAmount + " today. Your current balance is MMK " + currentAccount.getTotalBalence() + ".\n\n" +
                        "Thank you for your timely repayment.\n\n" +
                        "Best regards,\n" +
                        "RichCoin Financial Services";
                EmailSender.sendEmail(schedule.getSmeLoan().getCurrentAccount().getCif().getEmail(), emailSubject, emailBody);
            }
        }

        // Save updated data to the database
        currentAccountRepository.save(currentAccount);
        repaymentScheduleRepository.save(schedule);
    }
    private BigDecimal applyLateFee(SMERepaymentSchedule schedule) {
        System.out.println("Applying Late Fee");
        schedule.setLateFeeStatus(true);
        scheduleRepo.save(schedule);

        LocalDate dueDate = schedule.getDueDate();
        LocalDate graceEndDate = schedule.getGracePeriodEndDate();
        LocalDate currentDate = LocalDate.now();

        if (currentDate.equals(graceEndDate)) { // Late days start after grace period
            long lateDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);

            SMELateFeeCalculation lateFee = new SMELateFeeCalculation();
            lateFee.setSmeRepaymentSchedule(schedule);
            lateFee.setLateDays((int) lateDays);
            BigDecimal lateFeeBefore90Rate  = rateRepo.findValueByRateType("SME Late Fee Before 90 Days").divide(BigDecimal.valueOf(100));
            // Late fee based on days overdue
            BigDecimal lateFeeAmount = schedule.getInterestODAmount()
                    .multiply(lateFeeBefore90Rate) // Late fee rate
                    .multiply(BigDecimal.valueOf(lateDays)); // Multiply by late days

            lateFee.setLateFees(lateFeeAmount);
            lateFeeRepo.save(lateFee);
            return lateFeeAmount;
        }
        return null;
    }

}