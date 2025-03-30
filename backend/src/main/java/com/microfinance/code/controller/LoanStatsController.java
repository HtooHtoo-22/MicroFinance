package com.microfinance.code.controller;

import com.microfinance.code.service.impl.LoanStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanStatsController {

    @Autowired
    private LoanStatsService loanStatsService;

    @GetMapping("/approved-counts")
    public ResponseEntity<Map<String, Long>> getApprovedLoanCounts() {
        return ResponseEntity.ok(loanStatsService.getApprovedLoanCounts());
    }

    @GetMapping("/approved-counts/{branchId}")
    public ResponseEntity<Map<String, Long>> getApprovedLoanCountsByBranch(@PathVariable Integer branchId) {
        return ResponseEntity.ok(loanStatsService.getApprovedLoanCountsByBranch(branchId));
    }
}