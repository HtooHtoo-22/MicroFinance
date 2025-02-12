package com.microfinance.code.controller;

import com.microfinance.code.dto.CollateralDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.CollateralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/collaterals")
public class CollateralController {

    @Autowired
    private CollateralService collateralService;

//    @PostMapping("/")
//    public ApiResponse<CollateralDTO> createCollateral(@RequestPart("collateral") CollateralDTO dto,
//                                                       @RequestParam("image")MultipartFile image
//                                                      )
//    {
//        dto.setImageFile(image);
//        CollateralDTO createdCollateral = collateralService.createCollateral(dto);
//        return ApiResponse.success(HttpStatus.CREATED, 201, "Collateral created successfully", createdCollateral);
//    }
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
