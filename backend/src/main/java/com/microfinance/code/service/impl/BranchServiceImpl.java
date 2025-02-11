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
import io.netty.handler.codec.dns.AbstractDnsMessage;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Branch> optionalBranch = branchRepo.findById(id);
        if (!optionalBranch.isPresent()) {
            return null;
        }

        Branch branch = optionalBranch.get();

        // Update only non-null fields from DTO to the entity
        if (dto.getName() != null) {
            branch.setName(dto.getName());
        }
        if (dto.getAddress() != null) {
            branch.setAddress(dto.getAddress());
        }


        return branchRepo.save(branch);
    }


    @Override
    public ApiResponse<String> deleteBranch(Integer id) {
        // Logic to delete branch
        branchRepo.deleteById(id);
        return null;
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


