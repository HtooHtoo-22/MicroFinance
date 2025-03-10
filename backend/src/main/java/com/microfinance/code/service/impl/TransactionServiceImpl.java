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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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

        if (currentAccount.isFreezeStatus()) {
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
}

