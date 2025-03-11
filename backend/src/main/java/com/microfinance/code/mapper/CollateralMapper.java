package com.microfinance.code.mapper;

import com.microfinance.code.dto.CollateralDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.Collateral;
import com.microfinance.code.model.CollateralType;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.SMELoanHasCollateralRepo;
import com.microfinance.code.repository.SMELoanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class CollateralMapper {
    @Autowired
    private CurrentAccountRepository accRepo;

    private final SMELoanHasCollateralRepo loanHasCollateralRepo;

    public CollateralMapper(SMELoanHasCollateralRepo loanHasCollateralRepo) {
        this.loanHasCollateralRepo = loanHasCollateralRepo;
    }

    public Collateral toEntity(CollateralDTO dto) {
        if (dto == null) {
            return null;
        }

        Collateral collateral = new Collateral();
        collateral.setId(dto.getId());
        collateral.setValue(dto.getValue());
        collateral.setDescription(dto.getDescription());
        collateral.setAddress(dto.getAddress());
        collateral.setImage(dto.getImage());
        collateral.setName(dto.getName());
        // Setting related entities using IDs


        if (dto.getCollateralTypeId() != null) {
            CollateralType collateralType = new CollateralType();
            collateralType.setId(dto.getCollateralTypeId());
            collateral.setCollateralType(collateralType);
        }
        if(dto.getCurrentAccountId()!=null){
            CurrentAccount account = accRepo.findById(dto.getCurrentAccountId())
                    .orElseThrow(() -> new NotFoundException("Account Id not found with this: " + dto.getCurrentAccountId()));
            collateral.setCurrentAccount(account);
        }

        return collateral;
    }
    public CollateralDTO toDTO(Collateral collateral) {
        if (collateral == null) {
            return null;
        }

        CollateralDTO dto = new CollateralDTO();
        dto.setId(collateral.getId());
        dto.setValue(collateral.getValue());
        dto.setDescription(collateral.getDescription());
        dto.setAddress(collateral.getAddress());
        dto.setImage(collateral.getImage());
        dto.setName(collateral.getName());

        // Collateral type
        if (collateral.getCollateralType() != null) {
            dto.setCollateralTypeId(collateral.getCollateralType().getId());
            dto.setCollateralTypeName(collateral.getCollateralType().getName());
        }

        // Current account and CIF
        if (collateral.getCurrentAccount() != null && collateral.getCurrentAccount().getCif() != null) {
            dto.setCifId(collateral.getCurrentAccount().getCif().getCifId());
            dto.setOwnerName(collateral.getCurrentAccount().getCif().getUserName());
        } else {
            System.out.println("CurrentAccount or CIF is null for Collateral ID: {} "+collateral.getId());
        }

        // Remaining value
        BigDecimal usedValue = Optional.ofNullable(loanHasCollateralRepo.findTotalUsedValueByCollateralId(collateral.getId()))
                .orElse(BigDecimal.ZERO);
        BigDecimal collateralValue = Optional.ofNullable(collateral.getValue()).orElse(BigDecimal.ZERO);
        dto.setRemainingValue(collateralValue.subtract(usedValue));
        dto.setUsedValue(usedValue);
        // SME loan IDs
        List<String> smeLoanIds = Optional.ofNullable(
                loanHasCollateralRepo.getSMELoanIdListUsedByThisCollateral(collateral.getId())
        ).orElse(Collections.emptyList());
        dto.setSmeLoanIds(smeLoanIds);
        List<Integer> smeLoanPriIds = Optional.ofNullable(
                loanHasCollateralRepo.getSMELoanPrimaryIdListUsedByThisCollateral(collateral.getId())
        ).orElse(Collections.emptyList());
        dto.setSmeLoanPrimaryIds(smeLoanPriIds);
        return dto;
    }


}
