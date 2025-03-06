package com.microfinance.code.service.impl;

import com.microfinance.code.dto.CurrentAccountDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.CurrentAccountMapper;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.CIF;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.CIFRepo;
import com.microfinance.code.service.interFace.CurrentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


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
        dto.setFreezeStatus(true);
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

    @Override
    public List<CurrentAccountDTO> getAllCurrentACC() {
        return currentAccountRepository.findAll()
                .stream()
                .map(CurrentAccountMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String generateAccountId(Integer cifId) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Last 5 digits of timestamp
        return "ACC-" + cifId + "-" + timestamp;
    }

    @Override
    public long getCurrentAccountCountByBranch(Integer branchId) {
        return currentAccountRepository.countByBranchId(branchId);
    }


    @Override
    public CurrentAccountDTO updateCurrentAccount(String accountId, CurrentAccountDTO dto) {
        CurrentAccount existingAccount = currentAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with accountId: " + accountId));

        // Update the fields if they are provided
        if (dto.getMaxAmount() != null) existingAccount.setMaxAmount(dto.getMaxAmount());
        if (dto.getMinAmount() != null) existingAccount.setMinAmount(dto.getMinAmount());
        if (dto.getTotalBalance() != null) existingAccount.setTotalBalence(dto.getTotalBalance());
//        if (dto.isFreezeStatus()) existingAccount.setFreezeStatus(dto.isFreezeStatus());

        // If CIF ID is provided, update the CIF reference
        if (dto.getCifId() != null) {
            CIF cif = cifRepo.findById(dto.getCifId())
                    .orElseThrow(() -> new NotFoundException("CIF not found with id: " + dto.getCifId()));
            existingAccount.setCif(cif);
        }

        // Save and return updated entity
        CurrentAccount updatedAccount = currentAccountRepository.save(existingAccount);
        return CurrentAccountMapper.toDTO(updatedAccount);
    }

    @Override
    public CurrentAccountDTO updateFreezeStatus(String accountId, boolean freeze) {
        CurrentAccount account = currentAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with accountId: " + accountId));

        account.setFreezeStatus(freeze);
        CurrentAccount updatedAccount = currentAccountRepository.save(account);

        return CurrentAccountMapper.toDTO(updatedAccount);
    }


}
