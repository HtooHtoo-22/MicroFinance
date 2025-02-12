package com.microfinance.code.mapper;

import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.model.SMELoan;

import java.time.format.DateTimeFormatter;

public class SMELoanMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static SMELoanDTO toDTO(SMELoan smeLoan) {
        SMELoanDTO dto = new SMELoanDTO();
        dto.setId(smeLoan.getId());
        dto.setLoanId(smeLoan.getLoanId());
        dto.setLoanAmount(smeLoan.getLoanAmount());
        dto.setInterestRate(smeLoan.getInterestRate());
        dto.setGracePeriod(smeLoan.getGracePeriod());
        dto.setLoanPurpose(smeLoan.getLoanPurpose());

        dto.setRegisteredDate(smeLoan.getRegisteredDate() != null ? smeLoan.getRegisteredDate().format(DATE_FORMATTER) : null);
        dto.setApprovedDate(smeLoan.getApprovedDate() != null ? smeLoan.getApprovedDate().format(DATE_FORMATTER) : null);

        dto.setStatus(smeLoan.getStatus().getDisplayName());
        dto.setDocumentFee(smeLoan.getDocumentFee());
        dto.setServiceCharge(smeLoan.getServiceCharge());

        dto.setExpiredDate(smeLoan.getExpiredDate()); // LocalDateTime, no conversion needed
        dto.setDuration(smeLoan.getDuration());
        dto.setPrincipal(smeLoan.getPrincipal());

        dto.setEntryUserId(smeLoan.getEntryUser().getId());
        dto.setEntryUserName(smeLoan.getEntryUser().getName()); // Assuming User has a name field

        dto.setApprovedUserId(smeLoan.getApprovedUser().getId());
        dto.setApprovedUserName(smeLoan.getApprovedUser().getName());

        dto.setCurrentAccountId(smeLoan.getCurrentAccount().getId());
        dto.setCurrentAccountaccId(smeLoan.getCurrentAccount().getAccountId()); // Assuming CurrentAccount has accountNumber

        return dto;
    }

    public static SMELoan toEntity(SMELoanDTO dto) {
        SMELoan smeLoan = new SMELoan();
        smeLoan.setId(dto.getId());
        smeLoan.setLoanId(dto.getLoanId());
        smeLoan.setLoanAmount(dto.getLoanAmount());
        smeLoan.setInterestRate(dto.getInterestRate());
        smeLoan.setGracePeriod(dto.getGracePeriod());
        smeLoan.setLoanPurpose(dto.getLoanPurpose());
        smeLoan.setDocumentFee(dto.getDocumentFee());
        smeLoan.setServiceCharge(dto.getServiceCharge());
        smeLoan.setExpiredDate(dto.getExpiredDate());
        smeLoan.setDuration(dto.getDuration());
        smeLoan.setPrincipal(dto.getPrincipal());

        return smeLoan;
    }
}
