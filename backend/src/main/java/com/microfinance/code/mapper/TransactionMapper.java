package com.microfinance.code.mapper;

import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.Transaction;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.repository.CurrentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    public Transaction toEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate());

        CurrentAccount currentAccount = currentAccountRepository.findByAccountId(dto.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("CurrentAccount not found with accountId: " + dto.getCurrentAccountId()));
        transaction.setCurrentAccountId(currentAccount);

        return transaction;
    }

    public TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        dto.setCurrentAccountId(transaction.getCurrentAccountId().getAccountId());
        return dto;
    }
}
