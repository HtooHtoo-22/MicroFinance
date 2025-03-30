package com.microfinance.code.service.impl;

import com.microfinance.code.repository.HPLoanRepo;
import com.microfinance.code.repository.SMELoanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoanStatsService {

    @Autowired
    private SMELoanRepo smeLoanRepo;

    @Autowired
    private HPLoanRepo hpLoanRepo;

    public Map<String, Long> getApprovedLoanCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("smeLoans", smeLoanRepo.countApprovedSMELoans());
        counts.put("hpLoans", hpLoanRepo.countApprovedHPLoans());
        return counts;
    }

    public Map<String, Long> getApprovedLoanCountsByBranch(Integer branchId) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("smeLoans", smeLoanRepo.countApprovedSMELoansByBranch(branchId));
        counts.put("hpLoans", hpLoanRepo.countApprovedHPLoansByBranch(branchId));
        return counts;
    }
}