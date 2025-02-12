package com.microfinance.code.service.impl;

import com.microfinance.code.dto.CurrentAccountDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.CurrentAccountMapper;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.CIF;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.CIFRepo;
import com.microfinance.code.service.CurrentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentAccountServiceImpl implements CurrentAccountService {

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Autowired
    private CIFRepo cifRepo;

    @Override
    public CurrentAccountDTO createCurrentAccount(CurrentAccountDTO dto) {
        CIF cif = cifRepo.findById(dto.getCifId())
                .orElseThrow(() -> new NotFoundException("CIF not found with id: " + dto.getCifId()));

        String accountId = generateAccountId(dto.getCifId());
        dto.setAccountId(accountId);
        CurrentAccount account = CurrentAccountMapper.toEntity(dto);
        account.setCif(cif); // Set the CIF entity
        CurrentAccount savedAccount = currentAccountRepository.save(account);
        return CurrentAccountMapper.toDTO(savedAccount);
    }

    @Override
    public CurrentAccountDTO getCurrentAccountById(String accountId) {
        CurrentAccount account = currentAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with accountId: " + accountId));
        return CurrentAccountMapper.toDTO(account);
    }

    private String generateAccountId(Integer cifId) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Last 5 digits of timestamp
        return "ACC-" + cifId + "-" + timestamp;
    }
}
