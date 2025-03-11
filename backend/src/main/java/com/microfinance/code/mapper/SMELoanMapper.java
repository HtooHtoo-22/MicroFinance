package com.microfinance.code.mapper;

import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.model.Collateral;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.repository.SMELoanHasCollateralRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SMELoanMapper {

    private static final Logger logger = LoggerFactory.getLogger(SMELoanMapper.class);

    @Autowired
    private SMELoanHasCollateralRepo loanHasCollateralRepo;

    @Autowired
    private CollateralMapper collateralMapper;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SMELoanDTO toDTO(SMELoan smeLoan) {
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
        dto.setExpiredDate(smeLoan.getExpiredDate());
        dto.setDuration(smeLoan.getDuration());
        dto.setPrincipal(smeLoan.getPrincipal());

        dto.setEntryUserId(smeLoan.getEntryUser().getId());
        dto.setEntryUserName(smeLoan.getEntryUser().getName());

        if (smeLoan.getApprovedUser() != null) {
            dto.setApprovedUserId(smeLoan.getApprovedUser().getId());
            dto.setApprovedUserName(smeLoan.getApprovedUser().getName());
        }

        dto.setCurrentAccountId(smeLoan.getCurrentAccount() != null ? smeLoan.getCurrentAccount().getId() : null);
        dto.setCurrentAccountaccId(smeLoan.getCurrentAccount() != null ? smeLoan.getCurrentAccount().getAccountId() : null);
        dto.setBorrowerName(smeLoan.getCurrentAccount() != null && smeLoan.getCurrentAccount().getCif() != null
                ? smeLoan.getCurrentAccount().getCif().getUserName() : null);
        dto.setCifId(smeLoan.getCurrentAccount().getCif().getId());
        dto.setCifIdNumber(smeLoan.getCurrentAccount().getCif().getCifId());

        List<Collateral> collaterals = loanHasCollateralRepo.findCollateralsBySmeLoanId(dto.getId());
        if (collaterals != null && !collaterals.isEmpty()) {

            try {
                dto.setUsedCollaterals(collaterals.stream()
                        .map(collateralMapper::toDTO)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                logger.error("Error while mapping collaterals: {}", e.getMessage(), e);
            }
        } else {
            logger.debug("No collaterals found for SME loan ID: {}", dto.getId());
            dto.setUsedCollaterals(Collections.emptyList()); // Avoid null
        }

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
