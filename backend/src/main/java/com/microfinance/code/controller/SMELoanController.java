package com.microfinance.code.controller;

import com.microfinance.code.dto.CollateralTypeDTO;
import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.SMELoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sme-loans")
public class SMELoanController {

    @Autowired
    private SMELoanService smeLoanService;

    @PostMapping("/")
    public ApiResponse<SMELoanDTO> createLoan(@RequestBody SMELoanDTO dto) {
        SMELoanDTO createdLoan = smeLoanService.createSMELoan(dto);
        return ApiResponse.success(HttpStatus.CREATED, 201, "SME Loan Register Successfully", createdLoan);
    }
    @PostMapping("/approve/{id}")
    public ApiResponse approveLoan(@PathVariable("id")Integer loanId){
        smeLoanService.approveSMELoan(loanId);
        return ApiResponse.success(HttpStatus.OK,HttpStatus.OK.value(), "Successfully Approve");
    }
}
