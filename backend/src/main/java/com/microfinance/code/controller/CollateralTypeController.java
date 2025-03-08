package com.microfinance.code.controller;

import com.microfinance.code.dto.CollateralTypeDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.impl.CollateralTypeServiceImpl;
import com.microfinance.code.service.interFace.CollateralTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collateral-types")
public class CollateralTypeController {
    @Autowired
    private CollateralTypeService collateralTypeService;

    // Create Collateral Type
    @PostMapping("/create")
    public ApiResponse<CollateralTypeDTO> createCollateralType(@RequestBody CollateralTypeDTO dto) {
        CollateralTypeDTO createdDTO = collateralTypeService.createCollateralType(dto);
        return ApiResponse.success(HttpStatus.CREATED, 201, "Collateral Type created successfully", createdDTO);
    }

    // Get All Active Collateral Types
    @GetMapping
    public ApiResponse<List<CollateralTypeDTO>> getAllCollateralTypes() {
        List<CollateralTypeDTO> collateralTypes = collateralTypeService.getAllCollateralTypes();
        return ApiResponse.success(HttpStatus.OK, 200, "Collateral Types retrieved successfully", collateralTypes);
    }

    // Get Collateral Type by ID
    @GetMapping("/{id}")
    public ApiResponse<CollateralTypeDTO> getCollateralTypeById(@PathVariable Integer id) {
        CollateralTypeDTO collateralTypeDTO = collateralTypeService.getCollateralTypeById(id);
        return ApiResponse.success(HttpStatus.OK, 200, "Collateral Type retrieved successfully", collateralTypeDTO);
    }

    // Update Collateral Type
    @PutMapping("/{id}")
    public ApiResponse<CollateralTypeDTO> updateCollateralType(
            @PathVariable Integer id,
            @RequestBody CollateralTypeDTO dto
    ) {
        CollateralTypeDTO updatedDTO = collateralTypeService.updateCollateralType(id, dto);
        if (updatedDTO == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "Collateral Type not found");
        }
        return ApiResponse.success(HttpStatus.OK, 200, "Collateral Type updated successfully", updatedDTO);
    }

    // Soft Delete Collateral Type (Set status to false)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCollateralType(@PathVariable Integer id) {
        boolean deleted = collateralTypeService.deleteCollateralType(id);
        if (!deleted) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "Collateral Type not found");
        }
        return ApiResponse.success(HttpStatus.NO_CONTENT, 204, "Collateral Type deleted successfully");
    }
}
