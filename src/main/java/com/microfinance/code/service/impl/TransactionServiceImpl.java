package com.microfinance.code.service.impl;

import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.exception.AccountFrozenException;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.exception.ValidationException; // Import the ValidationException class
import com.microfinance.code.mapper.TransactionMapper;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.Transaction;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.TransactionRepository;
import com.microfinance.code.service.interFace.TransactionService;
import com.microfinance.code.status.transactionType;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Transactional
    @Override
    public TransactionDTO createTransaction(TransactionDTO dto) {
        CurrentAccount currentAccount = currentAccountRepository.findByAccountId(dto.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("CurrentAccount not found with accountId: " + dto.getCurrentAccountId()));

        if (!currentAccount.isFreezeStatus()) {
            throw new AccountFrozenException("This account is frozen, transactions cannot be created.");
        }

        // Validate if the amount exceeds the maxAmount
        if (dto.getAmount().doubleValue() > currentAccount.getMaxAmount()) {
            throw new ValidationException("Transaction amount exceeds the maximum allowed amount.");
        }

        if (dto.getType() == transactionType.CR) {
            // Validate if the total balance after the credit will exceed maxAmount
            double maxCreditAmount = currentAccount.getMaxAmount() - currentAccount.getTotalBalence();
            if (dto.getAmount().doubleValue() > maxCreditAmount) {
                throw new ValidationException("Transaction amount will cause balance to exceed the maximum allowed amount. You can credit up to " + maxCreditAmount + ".");
            }
            currentAccount.setTotalBalence(currentAccount.getTotalBalence() + dto.getAmount().doubleValue());
        } else if (dto.getType() == transactionType.DR) {
            // Validate if the balance will drop below minAmount
            double maxDebitAmount = currentAccount.getTotalBalence() - currentAccount.getMinAmount();
            if (dto.getAmount().doubleValue() > maxDebitAmount) {
                throw new ValidationException("Transaction amount will cause balance to drop below the minimum allowed amount. You can withdraw up to " + maxDebitAmount + ".");
            }
            currentAccount.setTotalBalence(currentAccount.getTotalBalence() - dto.getAmount().doubleValue());
        }

        currentAccountRepository.save(currentAccount);
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setCurrentAccountId(currentAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public List<TransactionDTO> getAllTransactionHistory(){
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByCifId(Integer cifId) {
        List<CurrentAccount> currentAccounts = currentAccountRepository.findByCif_Id(cifId);
        if (currentAccounts.isEmpty()) return Collections.emptyList();

        List<Transaction> transactions = transactionRepository.findByCurrentAccountIdIn(currentAccounts);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<TransactionDTO> getTransactionsByCurrentAccountId(String currentAccountId) {
        List<Transaction> transactions = transactionRepository.findByCurrentAccountId(currentAccountId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }



    @Override
    public byte[] generateTransactionReport(Integer transactionId) throws JRException {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Convert single transaction to list (Jasper requires Collection)
        List<Transaction> transactionList = Collections.singletonList(transaction);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transactionList);

        // Load Jasper template
        InputStream jasperStream = getClass().getResourceAsStream("/reports/TransactionReport.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

        // Set parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Transaction Report");
        parameters.put("accountId", transaction.getCurrentAccountId().getAccountId());
        parameters.put("accountBalance", transaction.getCurrentAccountId().getTotalBalence());
        parameters.put("transactionAmount", transaction.getAmount());
        parameters.put("transactionDate", transaction.getDate());
        parameters.put("transactionType", transaction.getType());

        // Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}

