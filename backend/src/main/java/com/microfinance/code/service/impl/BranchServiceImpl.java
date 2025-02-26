package com.microfinance.code.service.impl;


import com.microfinance.code.dto.BranchDTO;
import com.microfinance.code.dto.CollateralTypeDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.BranchMapper;

import com.microfinance.code.model.Branch;
import com.microfinance.code.model.CollateralType;
import com.microfinance.code.repository.BranchRepo;
import com.microfinance.code.service.interFace.BranchService;
import com.microfinance.code.status.BranchStatus;
import io.netty.handler.codec.dns.AbstractDnsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public  class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private BranchRepo branchRepo;

    @Override
    public void hello() {
        System.out.println("Hello");
    }

    @Override
    public Branch createBranch(BranchDTO dto) {
        // Logic to create a branch
        Branch branch = branchMapper.toEntity(dto);  // Assuming you map DTO to Entity
        return branchRepo.save(branch);
    }

    @Override
    public Branch updateBranch(Integer id, BranchDTO dto) {
        Branch existingBranch = branchRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // Update fields
        existingBranch.setCode(dto.getCode());
        existingBranch.setName(dto.getName());
        existingBranch.setAddress(dto.getAddress());
        existingBranch.setState(dto.getState());
        existingBranch.setTownship(dto.getTownship());

        return branchRepo.save(existingBranch);
    }

    @Override
    public ApiResponse<String> deleteBranch(Integer id) {
        try {
            branchRepo.deleteById(id);
            return ApiResponse.success(HttpStatus.OK, 200, "Branch deleted successfully", null);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Failed to delete branch");
        }
    }

    @Override
    public BranchDTO getBranchById(Integer id) {
        return branchRepo.findById(id)
                .map(branchMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
    }

    @Override
    public List<BranchDTO> getAllBranches() {
        List<Branch> branches = branchRepo.findAll();
        return branches.stream()
                .map(branchMapper::toDTO)
                .collect(Collectors.toList());
    }
}


