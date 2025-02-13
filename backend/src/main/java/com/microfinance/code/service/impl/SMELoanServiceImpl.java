package com.microfinance.code.service.impl;

import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.exception.ValidationException;
import com.microfinance.code.mapper.SMELoanMapper;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.interFace.SMELoanService;
import com.microfinance.code.status.LoanStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SMELoanServiceImpl implements SMELoanService {
    @Autowired
    private SMELoanRepo smeLoanRepository;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private CurrentAccountRepository currentAccountRepository;
    @Autowired
    private CollateralRepo collateralRepo;
    @Autowired
    private SMELoanHasCollateralRepo smeLoanHasCollateralRepo;

    @Override
    public SMELoanDTO createSMELoan(SMELoanDTO dto) {
        // Fetch entry user
        User entryUser = userRepository.findById(dto.getEntryUserId())
                .orElseThrow(() -> new NotFoundException("Entry user not found"));

        // Fetch approved user
        User approvedUser = userRepository.findById(dto.getApprovedUserId())
                .orElseThrow(() -> new NotFoundException("Approved user not found"));

        // Fetch current account
        CurrentAccount currentAcc = currentAccountRepository.findById(dto.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("Current account not found"));

        // Convert DTO to Entity
        SMELoan smeLoan = SMELoanMapper.toEntity(dto);
        smeLoan.setEntryUser(entryUser);
        smeLoan.setApprovedUser(approvedUser);
        smeLoan.setCurrentAccount(currentAcc);


        BigDecimal totalRemainingCollateralValue = calculateTotalRemainingCollateralValue(dto.getCollateralIds());

        // Validate if the collateral is enough for the loan amount
        if (totalRemainingCollateralValue.compareTo(smeLoan.getLoanAmount()) < 0) {
            throw new ValidationException("Your total collateral amount is not enough for your loan amount");
        }
        smeLoan = smeLoanRepository.save(smeLoan);
        // Save SME Loan Has Collateral records
        saveSMELoanHasCollateral(dto.getCollateralIds(), smeLoan);
        return SMELoanMapper.toDTO(smeLoan);

    }

    @Transactional
    @Override
    public void approveSMELoan(Integer smeLoanId) {
        SMELoan smeLoan = smeLoanRepository.findById(smeLoanId)
                .orElseThrow(() -> new NotFoundException("SME Loan with ID " + smeLoanId + " not found."));
        if (smeLoan.getCurrentAccount() == null) {
            throw new NotFoundException("Current Account not found for SME Loan ID " + smeLoanId);
        }
        smeLoan.setStatus(LoanStatus.APPROVE); // Change status to "Approved"
        smeLoan.setApprovedDate(LocalDateTime.now()); // Set approved date to current date
        smeLoanRepository.save(smeLoan); // Save the updated loan
    }


    private BigDecimal calculateTotalRemainingCollateralValue(List<Integer> collateralIds) {
        BigDecimal totalRemainingCollateralValue = BigDecimal.ZERO;

        for (Integer collateralId : collateralIds) {
            Collateral collateral = collateralRepo.findById(collateralId)
                    .orElseThrow(() -> new NotFoundException("Collateral Id not found " + collateralId));

            // Check if the collateral is already used
            SMELoanHasCollateral existingLoanCollateral = smeLoanHasCollateralRepo.findByCollateral(collateral);
            if (existingLoanCollateral != null) {
                BigDecimal totalUsedValue = smeLoanHasCollateralRepo.findTotalUsedValueByCollateralId(collateralId);
                totalRemainingCollateralValue = totalRemainingCollateralValue.add(collateral.getValue().subtract(totalUsedValue));
            } else {
                totalRemainingCollateralValue = totalRemainingCollateralValue.add(collateral.getValue());
            }
        }

        return totalRemainingCollateralValue;
    }

    private void saveSMELoanHasCollateral(List<Integer> collateralIds, SMELoan smeLoan) {
        BigDecimal remainingLoanAmount = smeLoan.getLoanAmount();
        for (Integer collateralId : collateralIds) {
            Collateral collateral = collateralRepo.findById(collateralId)
                    .orElseThrow(() -> new NotFoundException("Collateral Id not found " + collateralId));

            // Calculate the used value for this collateral
            BigDecimal usedValue = collateral.getValue().min(remainingLoanAmount);

            // Reduce the remaining loan amount
            remainingLoanAmount = remainingLoanAmount.subtract(usedValue);

            // Create the SMELoanHasCollateral entity
            SMELoanHasCollateral loanHasCollateral = new SMELoanHasCollateral();
            loanHasCollateral.setCollateral(collateral);
            loanHasCollateral.setSmeLoan(smeLoan);
            loanHasCollateral.setUsedValue(usedValue); // Set the correct used value

            // Save the record
            smeLoanHasCollateralRepo.save(loanHasCollateral);
        }
    }


}