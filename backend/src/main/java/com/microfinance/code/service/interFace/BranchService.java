package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.BranchDTO;
import com.microfinance.code.model.Branch;

import java.util.List;

public interface BranchService {
    public void hello();

        Branch createBranch(BranchDTO dto);
        Branch updateBranch(Integer branchId, BranchDTO dto);
        void deleteBranch(Integer branchId);
        Branch getBranchById(Integer branchId);
        List<BranchDTO> getAllBranches();
    }


