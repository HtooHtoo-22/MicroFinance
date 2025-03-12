package com.microfinance.code.mapper;

import com.microfinance.code.dto.SMEScheduleDTO;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.model.SMERepaymentSchedule;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Component
public class SMEScheduleMapper {
    @Autowired
    private SMELoanMapper smeLoanMapper;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public  SMEScheduleDTO toDTO(SMERepaymentSchedule entity) {
        SMEScheduleDTO dto = new SMEScheduleDTO();

        dto.setId(entity.getId());
        dto.setDueDate(entity.getDueDate() != null ? entity.getDueDate().toString() : null);
        dto.setTotalDays(entity.getTotalDays());
        dto.setTermNumber(entity.getTermNumber());
        dto.setPrincipal(entity.getPrincipal());
        dto.setInterestAmount(entity.getInterestAmount());
        dto.setInterestODAmount(entity.getInterestODAmount());
        dto.setTotalRepaidAmount(entity.getTotalRepaidAmount());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().getDisplayName() : null);
        dto.setGracePeriodEndDate(entity.getGracePeriodEndDate() != null ? entity.getGracePeriodEndDate().toString() : null);
        dto.setFullyPaidDate(entity.getFullyPaidDate() != null ? entity.getFullyPaidDate().toString() : null);
        dto.setLateFeeStatus(entity.isLateFeeStatus());
        dto.setSmeLoanId(entity.getSmeLoan() != null ? entity.getSmeLoan().getId() : null);

        dto.setSmeLoanDTO(smeLoanMapper.toDTO(entity.getSmeLoan()));

        return dto;
    }

    // === ✅ Convert DTO → Entity ===
    public  SMERepaymentSchedule toEntity(SMEScheduleDTO dto) {
        SMERepaymentSchedule entity = new SMERepaymentSchedule();

        entity.setId(dto.getId());
        entity.setDueDate(parseDate(dto.getDueDate()));
        entity.setTotalDays(dto.getTotalDays());
        entity.setTermNumber(dto.getTermNumber());
        entity.setPrincipal(dto.getPrincipal() != null ? dto.getPrincipal() : BigDecimal.ZERO);
        entity.setInterestAmount(dto.getInterestAmount() != null ? dto.getInterestAmount() : BigDecimal.ZERO);
        entity.setInterestODAmount(dto.getInterestODAmount() != null ? dto.getInterestODAmount() : BigDecimal.ZERO);
        entity.setTotalRepaidAmount(dto.getTotalRepaidAmount() != null ? dto.getTotalRepaidAmount() : BigDecimal.ZERO);
        entity.setStatus(dto.getStatus() != null ? RepaymentStatus.valueOf(dto.getStatus()) : null);
        entity.setGracePeriodEndDate(parseDate(dto.getGracePeriodEndDate()));
        entity.setFullyPaidDate(parseDate(dto.getFullyPaidDate()));
        entity.setLateFeeStatus(dto.isLateFeeStatus());

        if (dto.getSmeLoanId() != null) {
            SMELoan loan = new SMELoan();
            loan.setId(dto.getSmeLoanId());
            entity.setSmeLoan(loan); // reference only by ID
        }

        return entity;
    }

    // === Helper method for safe date parsing ===
    private  LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        return LocalDate.parse(dateStr, formatter);
    }
}
