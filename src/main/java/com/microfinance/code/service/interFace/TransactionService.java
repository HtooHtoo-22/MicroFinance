package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.model.CurrentAccount;
import net.sf.jasperreports.engine.JRException;

import java.util.List;

public interface TransactionService {
    TransactionDTO createTransaction(TransactionDTO dto);

    List<TransactionDTO> getAllTransactionHistory();

    List<TransactionDTO> getTransactionsByCifId(Integer cifId);
    List<TransactionDTO> getTransactionsByCurrentAccountId(String currentAccountId);
    byte[] generateTransactionReport(Integer transactionId) throws JRException;

}


