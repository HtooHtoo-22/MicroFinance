package com.microfinance.code.controller;

import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.service.interFace.SMELoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class SMELoanController {

    @Autowired
    private SMELoanService smeLoanService;

    @PostMapping("/register")
    public SMELoanDTO registerLoan(@RequestBody SMELoanDTO smeLoanDTO) {
        return smeLoanService.registerLoan(smeLoanDTO);
    }

    @PutMapping("/approve/{loanId}/{approvedUserId}")
    public SMELoanDTO approveLoan(@PathVariable Integer loanId, @PathVariable Integer approvedUserId) {
        return smeLoanService.approveLoan(loanId, approvedUserId);
    }
}
