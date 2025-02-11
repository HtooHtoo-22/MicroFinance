package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.BranchDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.model.Branch;

import java.util.List;

public interface BranchService {
    public void hello();

        Branch createBranch(BranchDTO dto);
        Branch updateBranch(Integer id, BranchDTO dto);
        ApiResponse<String> deleteBranch(Integer id);
        BranchDTO getBranchById(Integer id);
        List<BranchDTO> getAllBranches();
    }


