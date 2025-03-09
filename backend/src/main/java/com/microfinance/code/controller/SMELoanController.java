package com.microfinance.code.controller;

import com.microfinance.code.dto.CollateralTypeDTO;
import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.SMELoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/sme-loans")
public class SMELoanController {

    @Autowired
    private SMELoanService smeLoanService;

    @PostMapping("/register")
    public ApiResponse<SMELoanDTO> createLoan(@RequestBody SMELoanDTO dto) {
        System.out.println("SME DTO : "+dto);
        try {
            SMELoanDTO createdLoan = smeLoanService.createSMELoan(dto);
            return ApiResponse.success(HttpStatus.CREATED, 201, "SME Loan Register Successfully", createdLoan);
        } catch (Exception ex) {
            System.out.println("Error creating loan: " + ex.getMessage());
            ex.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "An unexpected error occurred: " + ex.getMessage());
        }
    }
    @PostMapping("/approve/{id}")
    public ApiResponse approveLoan(@PathVariable("id")Integer loanId){
        smeLoanService.approveSMELoan(loanId);
        return ApiResponse.success(HttpStatus.OK,HttpStatus.OK.value(), "Successfully Approve");
    }
    @PostMapping("/reject/{id}")
    public ApiResponse rejectLoan(@PathVariable("id")Integer loanId){
        smeLoanService.rejectSMELoan(loanId);
        return ApiResponse.success(HttpStatus.OK,HttpStatus.OK.value(), "Successfully Reject!");
    }
    @PostMapping("/repayPrincipal/{smeLoanId}")
        public ApiResponse repayPrincipal(@PathVariable("smeLoanId")Integer loanId, @RequestParam("repaidPrincipalAmount")BigDecimal repaidPrincipal){
        smeLoanService.repayPrincipal(loanId,repaidPrincipal);
        return null;
    }
}
