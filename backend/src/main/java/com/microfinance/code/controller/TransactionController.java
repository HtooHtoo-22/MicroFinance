package com.microfinance.code.controller;

import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.exception.AccountFrozenException;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.exception.ValidationException;
import com.microfinance.code.service.interFace.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

//    @PostMapping("/create")
//    public ApiResponse<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto) {
//        TransactionDTO createdTransaction = transactionService.createTransaction(dto);
//        return ApiResponse.success(HttpStatus.CREATED, 201, "Transaction created successfully", createdTransaction);
//    }


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TransactionDTO>> createTransaction(@RequestBody TransactionDTO dto) {
        try {
            TransactionDTO createdTransaction = transactionService.createTransaction(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(HttpStatus.CREATED, 201, "Transaction created successfully", createdTransaction));
        } catch (AccountFrozenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, 403, e.getMessage()));
        } catch (NotFoundException | ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, 400, e.getMessage()));
        }
    }

}
