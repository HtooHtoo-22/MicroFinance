package com.microfinance.code.controller;

import com.microfinance.code.dto.CollateralDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.CollateralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/collaterals")
public class CollateralController {

    @Autowired
    private CollateralService collateralService;
    @PostMapping("/create")
    public ApiResponse<CollateralDTO> createCollateral(
            @RequestParam("value") BigDecimal value,
            @RequestParam("description") String description,
            @RequestParam("address") String address,
            @RequestParam("collateralTypeId") Integer collateralTypeId,
            @RequestParam("currentAccountId") Integer currentAccountId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    )
    {
        CollateralDTO collateralDTO = new CollateralDTO();
        collateralDTO.setValue(value);
        collateralDTO.setDescription(description);
        collateralDTO.setAddress(address);
        collateralDTO.setCollateralTypeId(collateralTypeId);
        collateralDTO.setCurrentAccountId(currentAccountId);
        collateralDTO.setImageFile(imageFile);

        System.out.println("Collateral DTO : "+collateralDTO);
        CollateralDTO createdCollateral = collateralService.createCollateral(collateralDTO);
        return ApiResponse.success(HttpStatus.CREATED, 201, "Collateral created successfully", createdCollateral);
    }

    @GetMapping("/")
    public ApiResponse<List<CollateralDTO>> getAllCollaterals() {
        List<CollateralDTO> collaterals = collateralService.getAllCollaterals();
        return ApiResponse.success(HttpStatus.OK, 200, "Collaterals retrieved successfully", collaterals);
    }
    @GetMapping("/{id}")
    public ApiResponse<CollateralDTO> getCollateralById(@PathVariable Integer id) {
        CollateralDTO collateral = collateralService.getCollateralById(id);
        return ApiResponse.success(HttpStatus.OK, 200, "Collateral retrieved successfully", collateral);
    }
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCollateral(@PathVariable Integer id) {
        collateralService.deleteCollateral(id);
        return ApiResponse.success(HttpStatus.OK, 200, "Collateral deleted successfully");
    }
}
