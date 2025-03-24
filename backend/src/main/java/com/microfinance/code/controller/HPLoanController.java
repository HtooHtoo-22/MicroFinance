package com.microfinance.code.controller;

import com.microfinance.code.dto.HPLoanDTO;
import com.microfinance.code.dto.MonthlyHPLoanCountDTO;
import com.microfinance.code.dto.MonthlySMELoanCountDTO;
import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.User;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.interFace.HPLoanService;
import com.microfinance.code.service.interFace.SMELoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hp-loans")
public class HPLoanController {
    @Autowired
    private HPLoanService hpLoanService;

    @Autowired
    private UserRepo userRepo;

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
    public ApiResponse approveLoan(@PathVariable("id") Integer loanId) {
        // Retrieve the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, 401, "User not authenticated");
        }

        String username = authentication.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        // Pass the retrieved user's ID to the service
        hpLoanService.approveHPLoan(loanId, user.getId());

        return ApiResponse.success(HttpStatus.OK, HttpStatus.OK.value(), "Successfully Approved");
    }
    @GetMapping("/list")
    public ApiResponse<List<HPLoanDTO>> getAllHPLoans() {
        List<HPLoanDTO> loans = hpLoanService.getAllHPLoans();
        return ApiResponse.success(HttpStatus.OK, HttpStatus.OK.value(), "Successfully fetched all HP loans", loans);
    }

    @GetMapping("/approved")
    public ApiResponse<List<HPLoanDTO>> getApprovedHPLoans() {
        List<HPLoanDTO> loans = hpLoanService.getApprovedHPLoans();
        return ApiResponse.success(HttpStatus.OK, HttpStatus.OK.value(), "Successfully fetched all approved HP loans", loans);
    }

    @GetMapping("/{id}")
    public ApiResponse<HPLoanDTO> getHPLoanById(@PathVariable("id") Integer id) {
        HPLoanDTO loan = hpLoanService.getHPLoanById(id);
        if(loan == null){
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "HP loan not found for this id " + id);
        }
        return ApiResponse.success(HttpStatus.OK, HttpStatus.OK.value(), "Successfully fetched HP loan with ID: " + id, loan);
    }

    @GetMapping("/monthly-approved")
    public List<MonthlyHPLoanCountDTO> getMonthlyApprovedLoans() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer branchId = getBranchIdFromAuthentication(auth);
        return hpLoanService.getApprovedLoansByBranchMonthly(branchId);
    }

    private Integer getBranchIdFromAuthentication(Authentication auth) {
        // Since your User implements UserDetails
        User user = (User) auth.getPrincipal();
        return user.getBranch().getId();
    }
}