package com.microfinance.code.controller;

import com.microfinance.code.dto.*;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.SMELoanService;
import com.microfinance.code.service.interFace.SMERepaymentTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sme-loans")
public class SMELoanController {

    @Autowired
    private SMELoanService smeLoanService;

    @Autowired
    private SMERepaymentTrackService repaymentTrackService;

    @PostMapping("/register")
    public ApiResponse<SMELoanDTO> createLoan(@RequestBody SMELoanDTO dto) {
        System.out.println("SME DTO : "+dto);
        SMELoanDTO createdLoan = smeLoanService.createSMELoan(dto);
        return ApiResponse.success(HttpStatus.CREATED, 201, "SME Loan Register Successfully", createdLoan);
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
    @GetMapping("/loans/{branchId}")
    public ResponseEntity<ApiResponse<List<SMELoanDTO>>> getAllLoans(@PathVariable("branchId")Integer branchId) {
        List<SMELoanDTO> smeLoanDTOList = smeLoanService.getAllLoansByBranchId(branchId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, 200, "Loan Types retrieved successfully", smeLoanDTOList));
    }
    @GetMapping("/{id}")
    public ApiResponse<SMELoanDTO> getLoanById(@PathVariable Integer id) {
        SMELoanDTO loan = smeLoanService.getLoanById(id);
        return ApiResponse.success(HttpStatus.OK, 200, "SME Loan retrieved successfully", loan);
    }
    @GetMapping("/getByLoanID/{id}")
    public ApiResponse<SMELoanDTO> getLoanByLoanId(@PathVariable String id) {
        SMELoanDTO loan = smeLoanService.getLoanByLoanId(id);
        return ApiResponse.success(HttpStatus.OK, 200, "SME Loan retrieved successfully", loan);
    }
    @GetMapping("getRepaymentTracks/{loanId}")
    public ApiResponse<List<SMERepaymentTrackDTO>> getRepaymentTracksByLoanId(@PathVariable("loanId")Integer loanId){
        List<SMERepaymentTrackDTO> repayTrackList = repaymentTrackService.getTrackListByLoanId(loanId);
        return ApiResponse.success(HttpStatus.OK, 200, "SME Repay Tracks retrieved successfully", repayTrackList);
    }
    @GetMapping("/getLateFeeSummary/{loanId}")
    public ApiResponse<SMELateFeeSummaryDTO> getLateFeeSummaryByLoanId(@PathVariable("loanId")Integer loanId){
        SMELateFeeSummaryDTO lateFeeSummaryDTO = smeLoanService.getLateFeeAndODByLoanId(loanId);
        return ApiResponse.success(HttpStatus.OK, 200, "SME OD and Late Fee Summary retrieved successfully", lateFeeSummaryDTO);
    }
    @GetMapping("/approved-loans/{branchId}")
    public ResponseEntity<ApiResponse<List<SMELoanDTO>>> getApprovedLoans(@PathVariable("branchId")Integer branchId) {
        List<SMELoanDTO> smeLoanDTOList = smeLoanService.getApprovedLoansByBranchId(branchId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, 200, "SME Approve loan list retrieved. ", smeLoanDTOList));
    }
    @GetMapping("/pending-loans/{branchId}")
    public ResponseEntity<ApiResponse<List<SMELoanDTO>>> getPendingLoans(@PathVariable("branchId")Integer branchId) {
        List<SMELoanDTO> smeLoanDTOList = smeLoanService.getPendingLoansByBranchId(branchId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, 200, "SME Pending loan list retrieved. ", smeLoanDTOList));
    }
}
