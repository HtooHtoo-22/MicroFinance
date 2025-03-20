package com.microfinance.code.controller;

import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.exception.AccountFrozenException;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.exception.ValidationException;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.service.interFace.TransactionService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

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

    @GetMapping("/list")
    public ApiResponse<List<TransactionDTO>> getAllTransaction() {
        List<TransactionDTO> transactionDTO = transactionService.getAllTransactionHistory();
        return ApiResponse.success(HttpStatus.OK, 200, "Transaction retrieved successfully", transactionDTO);
    }

    @GetMapping("/by-cif/{cifId}")
    public ApiResponse<List<TransactionDTO>> getTransactionsByCifId(@PathVariable Integer cifId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByCifId(cifId);
        return ApiResponse.success(HttpStatus.OK, 200, "Transactions retrieved", transactions);
    }

    @GetMapping("/transactions/{currentAccountId}")
    public ApiResponse<List<TransactionDTO>> getTransactionsByCurrentAccountId(@PathVariable String currentAccountId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByCurrentAccountId(currentAccountId);
        return ApiResponse.success(HttpStatus.OK, 200, "Transactions retrieved successfully", transactions);
    }


    @GetMapping("/download-report/{transactionId}")
    public ResponseEntity<byte[]> downloadTransactionReport(@PathVariable Integer transactionId) throws JRException, IOException {
        byte[] reportContent = transactionService.generateTransactionReport(transactionId);

        if (reportContent == null || reportContent.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("TransactionReport.pdf").build());

        System.out.println("transaction id :"+transactionId);

        return ResponseEntity.ok().headers(headers).body(reportContent);
    }
}
