package com.microfinance.code.service;

import com.microfinance.code.dto.TransactionDTO;

public interface TransactionService {
    TransactionDTO createTransaction(TransactionDTO dto);
}
