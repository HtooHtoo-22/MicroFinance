package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.model.CurrentAccount;

import java.util.List;

public interface TransactionService {
    TransactionDTO createTransaction(TransactionDTO dto);

    List<TransactionDTO> getAllTransactionHistory();

    List<TransactionDTO> getTransactionsByCifId(Integer cifId);
}
