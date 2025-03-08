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

    @Autowired
    private SMELoanHasCollateralRepo loanHasCollateralRepo;
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
        System.out.println("Mapper called");  // Check if the mapper is called
        if (collateral == null) {
            return null;
        }

        CollateralDTO dto = new CollateralDTO();
        dto.setId(collateral.getId());
        dto.setValue(collateral.getValue());
        dto.setDescription(collateral.getDescription());
        dto.setAddress(collateral.getAddress());
        dto.setImage(collateral.getImage());

        // Check collateral type
        if (collateral.getCollateralType() != null) {
            System.out.println("CollateralType: " + collateral.getCollateralType().getName());
            dto.setCollateralTypeId(collateral.getCollateralType().getId());
            dto.setCollateralTypeName(collateral.getCollateralType().getName());
        } else {
            System.out.println("CollateralType is null");
        }

        // Check current account and CIF details
        if (collateral.getCurrentAccount() != null && collateral.getCurrentAccount().getCif() != null) {
            System.out.println("CIF ID: " + collateral.getCurrentAccount().getCif().getCifId());
            System.out.println("Owner Name: " + collateral.getCurrentAccount().getCif().getUserName());
            dto.setCifId(collateral.getCurrentAccount().getCif().getCifId());
            dto.setOwnerName(collateral.getCurrentAccount().getCif().getUserName());
        } else {
            System.out.println("CurrentAccount or CIF is null");
        }

        // Check remaining value calculation
        BigDecimal usedValue = loanHasCollateralRepo.findTotalUsedValueByCollateralId(collateral.getId());
        System.out.println("Used Value: " + usedValue);  // Print the used value
        BigDecimal remainingValue = collateral.getValue().subtract(usedValue);
        System.out.println("Remaining Value: " + remainingValue);  // Print the remaining value
        dto.setRemainingValue(remainingValue);

        // Check loan IDs
        List<String> smeLoanIds = Optional.ofNullable(loanHasCollateralRepo.getSMELoanIdListUsedByThisCollateral(collateral.getId()))
                .orElse(Collections.emptyList());
        System.out.println("SME Loan IDs: " + smeLoanIds);  // Print the SME loan IDs
        dto.setSmeLoanIds(smeLoanIds);

        return dto;
    }

}
