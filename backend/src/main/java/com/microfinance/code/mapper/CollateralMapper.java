package com.microfinance.code.mapper;

import com.microfinance.code.dto.CollateralDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.Collateral;
import com.microfinance.code.model.CollateralType;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.SMELoanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CollateralMapper {
    @Autowired
    private CurrentAccountRepository accRepo;
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
        if (collateral == null) {
            return null;
        }

        CollateralDTO dto = new CollateralDTO();
        dto.setId(collateral.getId());
        dto.setValue(collateral.getValue());
        dto.setDescription(collateral.getDescription());
        dto.setAddress(collateral.getAddress());
        dto.setImage(collateral.getImage());

        if (collateral.getCollateralType() != null) {
            dto.setCollateralTypeId(collateral.getCollateralType().getId());
            dto.setCollateralTypeName(collateral.getCollateralType().getName()); // Fetching name
        }

        return dto;
    }
}
