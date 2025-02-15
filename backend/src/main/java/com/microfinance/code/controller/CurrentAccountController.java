package com.microfinance.code.controller;

import com.microfinance.code.dto.CurrentAccountDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.CurrentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

}
