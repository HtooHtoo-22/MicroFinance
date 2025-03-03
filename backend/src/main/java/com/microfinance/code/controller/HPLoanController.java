package com.microfinance.code.controller;

import com.microfinance.code.dto.HPLoanDTO;
import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.HPLoanService;
import com.microfinance.code.service.interFace.SMELoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hp-loans")
public class HPLoanController {
    @Autowired
    private HPLoanService hpLoanService;

    @PostMapping("/register")
    public ApiResponse<HPLoanDTO> createLoan(@RequestBody HPLoanDTO dto) {
        HPLoanDTO createdLoan = hpLoanService.createSMELoan(dto);
        return ApiResponse.success(HttpStatus.CREATED, 201, "HP Loan Register Successfully", createdLoan);
    }
    @PostMapping("/reject/{id}")
    public ApiResponse rejectLoan(@PathVariable("id")Integer loanId){
        hpLoanService.rejectHPLoan(loanId);
        return ApiResponse.success(HttpStatus.OK,HttpStatus.OK.value(), "Successfully Reject!");
    }
    @PostMapping("/approve/{id}")
    public ApiResponse approveLoan(@PathVariable("id")Integer loanId){
        hpLoanService.approveHPLoan(loanId,1);
        return ApiResponse.success(HttpStatus.OK,HttpStatus.OK.value(), "Successfully Approve");
    }
}