package com.microfinance.code.controller;

import com.microfinance.code.dto.BranchDTO;

import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.mapper.BranchMapper;
import com.microfinance.code.model.Branch;
import com.microfinance.code.service.interFace.BranchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@CrossOrigin
public class BranchController {

    private final BranchService branchService;
    private final BranchMapper branchMapper;

    public BranchController(BranchService branchService, BranchMapper branchMapper) {
        this.branchService = branchService;
        this.branchMapper = branchMapper;
    }

    @PostMapping
    public ApiResponse<BranchDTO> createBranch(@RequestBody BranchDTO dto) {
        Branch branch = branchMapper.toEntity(dto);
        Branch createdBranch = branchService.createBranch(dto);
        BranchDTO createdBranchDTO = branchMapper.toDTO(createdBranch);
        return ApiResponse.success(HttpStatus.CREATED, 101, "Branch created successfully", createdBranchDTO);
    }
    @GetMapping
    public ApiResponse<List<BranchDTO>> getAllBranches() {
        List<BranchDTO> branchList = branchService.getAllBranches();
        return ApiResponse.success(HttpStatus.OK, 200, "Branch Types retrieved successfully", branchList);
    }


    @GetMapping("/{id}")
    public ApiResponse<BranchDTO> getBranchById(@PathVariable Integer id) {
        BranchDTO branchDTO = branchService.getBranchById(id);
        return   ApiResponse.success(HttpStatus.OK, 200, "Collateral Type retrieved successfully", branchDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchDTO>> updateBranch(
            @PathVariable Integer id,
            @RequestBody BranchDTO dto
    ) {
        Branch updatedBranch = branchService.updateBranch(id, dto);

        if (updatedBranch == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, 404, "Branch not found"));
        }

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, 200, "Branch updated successfully", branchMapper.toDTO(updatedBranch))
        );

    }


    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBranch(@PathVariable Integer id) {
        branchService.deleteBranch(id);
        return ApiResponse.success(HttpStatus.OK, 200, "Branch deleted successfully");
    }
}
