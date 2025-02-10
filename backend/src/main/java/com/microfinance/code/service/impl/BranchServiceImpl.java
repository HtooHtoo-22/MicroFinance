package com.microfinance.code.service.impl;

import com.microfinance.code.dto.BranchDTO;
import com.microfinance.code.mapper.BranchMapper;
import com.microfinance.code.model.Branch;
import com.microfinance.code.repository.BranchRepo;
import com.microfinance.code.service.interFace.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public Branch updateBranch(Integer branchId, BranchDTO dto) {
        // Logic to update branch
        Branch existingBranch = branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        // Update fields and save
        existingBranch.setName(dto.getName()); // Example field update
        return branchRepo.save(existingBranch);
    }

    @Override
    public void deleteBranch(Integer branchId) {
        // Logic to delete branch
        branchRepo.deleteById(branchId);
    }

    @Override
    public Branch getBranchById(Integer branchId) {
        // Logic to fetch branch by ID
        return branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
    }
    @Override
    public List<BranchDTO> getAllBranches() {
        List<Branch> branches = branchRepo.findAll();
        return branches.stream()
                .map(branchMapper::toDTO)
                .collect(Collectors.toList());
    }




}


