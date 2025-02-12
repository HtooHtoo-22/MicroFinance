package com.microfinance.code.controller;

import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/create")
    public ApiResponse<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto) {
        TransactionDTO createdTransaction = transactionService.createTransaction(dto);
        return ApiResponse.success(HttpStatus.CREATED, 201, "Transaction created successfully", createdTransaction);
    }
}
