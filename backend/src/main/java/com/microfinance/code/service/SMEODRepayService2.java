package com.microfinance.code.service;
import com.microfinance.code.model.SMEODRepaymentTrack;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.SMELateFeeCalculationRepo;
import com.microfinance.code.repository.SMEODRepaymentTrackRepo;
import com.microfinance.code.repository.SMERepaymentScheduleRepo;
import com.microfinance.code.status.RepaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SMEODRepayService2 {
    @Autowired
    private  SMERepaymentScheduleRepo scheduleRepo;
    @Autowired
    private  SMEODRepaymentTrackRepo repaymentTrackRepo;
    @Autowired
    private  SMELateFeeCalculationRepo lateFeeRepo;


    @Transactional
    public void processODRepayment(BigDecimal transactionAmount, Integer smeLoanId) {
        System.out.println("Processing OD Repayment for SME Loan ID: " + smeLoanId);

        List<SMERepaymentSchedule> overdueSchedules = scheduleRepo.findBySmeLoanIdAndStatusIn(
                smeLoanId, List.of(RepaymentStatus.PARTIAL_OVERDUE, RepaymentStatus.FULL_OVERDUE));

        if (overdueSchedules.isEmpty()) {
            System.out.println("No overdue schedules found for SME Loan ID: " + smeLoanId);
            return;
        }

        BigDecimal remainingTransactionAmount = transactionAmount;

        for (SMERepaymentSchedule schedule : overdueSchedules) {
            if (remainingTransactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal amountToRepay = remainingTransactionAmount.min(schedule.getInterestODAmount());
            remainingTransactionAmount = remainingTransactionAmount.subtract(amountToRepay);

            updateRepaymentStatus(schedule, amountToRepay);
            logRepayment(schedule, amountToRepay);
        }

        if (remainingTransactionAmount.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("Excess transaction amount remaining: " + remainingTransactionAmount);
        }
    }

    private void updateRepaymentStatus(SMERepaymentSchedule schedule, BigDecimal amountToRepay) {
        BigDecimal remainingAmount = schedule.getInterestODAmount().subtract(amountToRepay);
        schedule.setInterestODAmount(remainingAmount);
        schedule.setTotalRepaidAmount(schedule.getTotalRepaidAmount().add(amountToRepay));

        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            schedule.setStatus(RepaymentStatus.PAID);
            schedule.setFullyPaidDate(LocalDate.now());
        } else {
            schedule.setStatus(RepaymentStatus.PARTIAL_OVERDUE);
        }

        scheduleRepo.save(schedule);
    }

    private void logRepayment(SMERepaymentSchedule schedule, BigDecimal amountToRepay) {
        SMEODRepaymentTrack track = new SMEODRepaymentTrack();
        track.setSmeRepaymentSchedule(schedule);
        track.setPaid_od_amount(amountToRepay);
        track.setDate(LocalDateTime.now());
        repaymentTrackRepo.save(track);
    }
}
