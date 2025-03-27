package com.microfinance.code.controller;

import com.microfinance.code.dto.CurrentAccountDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.CurrentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class CurrentAccountController {

    @Autowired
    private CurrentAccountService currentAccountService;

    @PostMapping("/currentAcc")
    public ApiResponse<CurrentAccountDTO> createAccount(@RequestBody CurrentAccountDTO dto) {
        CurrentAccountDTO createcurrentACC =  currentAccountService.createCurrentAccount(dto);
        return ApiResponse.success(HttpStatus.CREATED, 201, "Current Account created successfully", createcurrentACC);
    }

    @GetMapping("/{accountId}")
    public ApiResponse<CurrentAccountDTO> getAccount(@PathVariable String accountId) {
        CurrentAccountDTO getACCDTO =  currentAccountService.getCurrentAccountById(accountId);
        return ApiResponse.success(HttpStatus.OK, 200, "Current Account received successfully", getACCDTO);
    }

    @PutMapping("/{accountId}")
    public ApiResponse<CurrentAccountDTO> updateAccount(
            @PathVariable String accountId,
            @RequestBody CurrentAccountDTO dto
    ) {
        CurrentAccountDTO updatedAccount = currentAccountService.updateCurrentAccount(accountId, dto);
        return ApiResponse.success(HttpStatus.OK, 200, "Account updated successfully", updatedAccount);
    }

    @GetMapping
    public ApiResponse<List<CurrentAccountDTO>> getAllCurrentACC() {
        List<CurrentAccountDTO> currentAccountDTOS = currentAccountService.getAllCurrentACC();
        return ApiResponse.success(HttpStatus.OK, 200, "Current Accounts retrieved successfully", currentAccountDTOS);
    }

    @GetMapping("/by-cif/{cifId}") // Ensure this matches the endpoint you're calling
    public ApiResponse<List<CurrentAccountDTO>> getAccountsByCifId(@PathVariable Integer cifId) {
        List<CurrentAccountDTO> accounts = currentAccountService.getAccountsByCifId(cifId);
        return ApiResponse.success(HttpStatus.OK, 200, "Accounts retrieved successfully", accounts);
    }

    @GetMapping("/count/{branchId}")
    public long getCurrentAccountCount(@PathVariable Integer branchId) {
        return currentAccountService.getCurrentAccountCountByBranch(branchId);
    }

    @PutMapping("/currentAcc/{accountId}")
    public ApiResponse<CurrentAccountDTO> updateCurrentAccount(@PathVariable String accountId, @RequestBody CurrentAccountDTO dto) {
        CurrentAccountDTO updatedAccount = currentAccountService.updateCurrentAccount(accountId, dto);
        return ApiResponse.success(HttpStatus.OK, 200, "Current Account updated successfully", updatedAccount);
    }

    @PatchMapping("/{accountId}/freeze")
    public ApiResponse<CurrentAccountDTO> freezeAccount(@PathVariable String accountId, @RequestParam boolean freeze) {
        CurrentAccountDTO updatedAccount = currentAccountService.updateFreezeStatus(accountId, freeze);
        String message = freeze ? "Account frozen successfully" : "Account unfrozen successfully";
        return ApiResponse.success(HttpStatus.OK, 200, message, updatedAccount);
    }

}