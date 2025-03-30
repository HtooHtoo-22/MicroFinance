package com.microfinance.code.service.impl;

import com.microfinance.code.dto.CurrentAccountDTO;
import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.CurrentAccountMapper;
import com.microfinance.code.mapper.TransactionMapper;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.CIF;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.CIFRepo;
import com.microfinance.code.repository.TransactionRepository;
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

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public CurrentAccountDTO createCurrentAccount(CurrentAccountDTO dto) {
        CIF cif = cifRepo.findById(dto.getCifId())
                .orElseThrow(() -> new NotFoundException("CIF not found with id: " + dto.getCifId()));

        String accountId = generateAccountId(dto.getCifId());
        dto.setAccountId(accountId);
        dto.setCifCode(cif.getCifId());
        System.out.println("Cif code"+ cif.getCifId());
        CurrentAccount account = CurrentAccountMapper.toEntity(dto);


        System.out.println("Generated Account ID: " + accountId); // ✅ Log Account ID
        System.out.println("Retrieved CIF Code: " + cif.getCifId());
        account.setCif(cif); // Set the CIF entity
        account.setFreezeStatus(false);
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
    public List<CurrentAccountDTO> getAllCurrentACC() {
        return currentAccountRepository.findAll()
                .stream()
                .map(CurrentAccountMapper::toDTO)
                .collect(Collectors.toList());
    }


    // CurrentAccountServiceImpl.java
    @Override
    public List<CurrentAccountDTO> getAccountsByCifId(Integer cifId) {
        System.out.println("Fetching accounts for CIF ID: " + cifId); // Log the CIF ID
        List<CurrentAccount> accounts = currentAccountRepository.findByCif_Id(cifId);
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for CIF ID: " + cifId); // Log if no accounts are found
        }
        return accounts.stream()
                .map(CurrentAccountMapper::toDTO)
                .collect(Collectors.toList());
    }



    @Override
    public CurrentAccountDTO updateFreezeStatus(String accountId, boolean freeze) {
        CurrentAccount account = currentAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with accountId: " + accountId));

        account.setFreezeStatus(freeze);
        CurrentAccount updatedAccount = currentAccountRepository.save(account);

        return CurrentAccountMapper.toDTO(updatedAccount);
    }

    // CurrentAccountServiceImpl.java
    @Override
    public long getActiveCurrentAccountCountByBranch(Integer branchId) {
        return currentAccountRepository.countActiveAccountsByBranchId(branchId);
    }

    @Override
    public long countActiveAccounts() {
        return currentAccountRepository.countActiveAccounts();
    }

    @Override
    public List<CurrentAccountDTO> getAllCurrentACCByBranchId(Integer branchId){
        List<CurrentAccount> accounts = currentAccountRepository.findByCif_Branch_Id(branchId);
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for Branch ID: " + branchId); // Log if no accounts are found
        }
        return accounts.stream()
                .map(CurrentAccountMapper::toDTO)
                .collect(Collectors.toList());
    }

//    @Override
//    // TransactionService.java
//    public List<TransactionDTO> getTransactionsByCurrentAccountId(String currentAccountId) {
//        CurrentAccount currentAccount = currentAccountRepository.findByAccountId(currentAccountId)
//                .orElseThrow(() -> new NotFoundException("Current account not found with ID: " + currentAccountId));
//        return transactionRepo
//                .findByCurrentAccount(currentAccount)
//                .stream()
//                .map(transactionMapper::toDTO)
//                .collect(Collectors.toList());
//    }
}