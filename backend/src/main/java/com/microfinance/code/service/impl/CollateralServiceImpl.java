package com.microfinance.code.service.impl;

import com.microfinance.code.dto.CollateralDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.CollateralMapper;
import com.microfinance.code.model.Collateral;
import com.microfinance.code.model.CollateralType;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.repository.CollateralRepo;
import com.microfinance.code.repository.CollateralTypeRepo;
import com.microfinance.code.repository.SMELoanRepo;
import com.microfinance.code.service.CloudinaryService;
import com.microfinance.code.service.interFace.CollateralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollateralServiceImpl implements CollateralService {

    @Autowired
    private CollateralRepo collateralRepo;

    @Autowired
    private CollateralMapper collateralMapper;

    @Autowired
    private SMELoanRepo smeLoanRepo;

    @Autowired
    private CollateralTypeRepo collateralTypeRepo;

    private CloudinaryService cloudinaryService;
    public CollateralDTO createCollateral(CollateralDTO dto) {
        try {
            dto.setImage(cloudinaryService.uploadFile(dto.getImageFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Collateral collateral = collateralMapper.toEntity(dto);

        // Validate Collateral Type
        CollateralType collateralType = collateralTypeRepo.findById(dto.getCollateralTypeId())
                .orElseThrow(() -> new NotFoundException("Collateral Type not found with ID: " + dto.getCollateralTypeId()));
        collateral.setCollateralType(collateralType);

        Collateral savedCollateral = collateralRepo.save(collateral);
        return collateralMapper.toDTO(savedCollateral);
    }
    public List<CollateralDTO> getAllCollaterals() {
        List<Collateral> collaterals = collateralRepo.findAll();
        return collaterals.stream()
                .map(collateralMapper::toDTO)
                .collect(Collectors.toList());
    }
    public CollateralDTO getCollateralById(Integer id) {
        Collateral collateral = collateralRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Collateral not found with ID: " + id));
        return collateralMapper.toDTO(collateral);
    }
    public void deleteCollateral(Integer id) {
        Collateral collateral = collateralRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Collateral not found with ID: " + id));
        collateralRepo.delete(collateral);
    }
}
