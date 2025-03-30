package com.microfinance.code.service.impl;

import com.microfinance.code.dto.CollateralDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.CollateralMapper;
import com.microfinance.code.model.Collateral;
import com.microfinance.code.model.CollateralType;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.repository.CollateralRepo;
import com.microfinance.code.repository.CollateralTypeRepo;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.SMELoanRepo;
import com.microfinance.code.service.CloudinaryService;
import com.microfinance.code.service.interFace.CollateralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CurrentAccountRepository accountRepo;
    @Override
    public CollateralDTO createCollateral(CollateralDTO dto) {
        try {
            // Upload image asynchronously
            CompletableFuture<String> imageUrlFuture = cloudinaryService.uploadFileAsync(dto.getImageFile());

            // Wait for the upload to complete and get the URL
            String imageUrl = imageUrlFuture.get(); // Blocking call to wait for the result
            dto.setImage(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image to Cloudinary", e);
        }

        // Convert DTO to Entity
        Collateral collateral = collateralMapper.toEntity(dto);

        // Validate Collateral Type
        CollateralType collateralType = collateralTypeRepo.findById(dto.getCollateralTypeId())
                .orElseThrow(() -> new NotFoundException("Collateral Type not found with ID: " + dto.getCollateralTypeId()));
        collateral.setCollateralType(collateralType);

        // Save to DB
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

    @Override
    public List<CollateralDTO> getCollateralByAccId(String id) {
        CurrentAccount acc = accountRepo.findByAccountId(id)
                .orElseThrow(()->new NotFoundException("Current Account Not Found With This ID : "+id));
        List<Collateral> collaterals = collateralRepo.findByCurrentAccount(acc);
        return collaterals.stream()
                .map(collateralMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollateralDTO> getCollateralsByBranchId(Integer id){
        List<Collateral> collaterals = collateralRepo.findByCurrentAccount_Cif_Branch_Id(id);
        return collaterals.stream()
                .map(collateralMapper::toDTO)
                .collect(Collectors.toList());
    }
}
