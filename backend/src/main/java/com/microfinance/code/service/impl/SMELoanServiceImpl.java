package com.microfinance.code.service.impl;

import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.mapper.SMELoanMapper;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.model.User;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.SMELoanRepo;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.interFace.SMELoanService;
import com.microfinance.code.status.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public abstract class SMELoanServiceImpl implements SMELoanService {

    @Autowired
    private SMELoanRepo smeLoanRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Override
    public SMELoanDTO registerLoan(SMELoanDTO smeLoanDTO, int entryUserId, int currentAccountId) {
        SMELoan smeLoan = SMELoanMapper.toEntity(smeLoanDTO);

        // Set relationships (entryUser and currentAccount)
        User entryUser = userRepository.findById(entryUserId)
                .orElseThrow(() -> new RuntimeException("Entry user not found")); // Handle appropriately
        smeLoan.setEntryUser(entryUser);

        CurrentAccount currentAccount = currentAccountRepository.findById(currentAccountId)
                .orElseThrow(() -> new RuntimeException("Current account not found")); // Handle appropriately
        smeLoan.setCurrentAccount(currentAccount);


        smeLoan = smeLoanRepository.save(smeLoan);
        return SMELoanMapper.toDTO(smeLoan);
    }

    @Override
    public SMELoanDTO approveLoan(String loanId, int approvedUserId) {
        Optional<SMELoan> optionalLoan = smeLoanRepository.findByLoanId(loanId);

        if (optionalLoan.isPresent()) {
            SMELoan smeLoan = optionalLoan.get();

            // Set approved user
            User approvedUser = userRepository.findById(approvedUserId)
                    .orElseThrow(() -> new RuntimeException("Approved user not found"));
            smeLoan.setApprovedUser(approvedUser);
            smeLoan.setApprovedDate(LocalDateTime.now());
            smeLoan.setStatus(LoanStatus.APPROVE);

            smeLoanRepository.save(smeLoan);
            return SMELoanMapper.toDTO(smeLoan);
        } else {
            throw new RuntimeException("Loan not found with ID: " + loanId); // Or a custom exception
        }
    }


}