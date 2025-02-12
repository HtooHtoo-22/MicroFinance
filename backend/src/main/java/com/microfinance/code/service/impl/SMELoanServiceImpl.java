package com.microfinance.code.service.impl;

import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.SMELoanMapper;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.SMELoan;
import com.microfinance.code.model.User;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.SMELoanRepo;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.interFace.SMELoanService;
import com.microfinance.code.status.LoanStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SMELoanServiceImpl implements SMELoanService {
    @Autowired
    private SMELoanRepo smeLoanRepository;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Override
    public SMELoanDTO createSMELoan(SMELoanDTO dto) {
        // Fetch entry user
        User entryUser = userRepository.findById(dto.getEntryUserId())
                .orElseThrow(() -> new NotFoundException("Entry user not found"));

        // Fetch approved user
        User approvedUser = userRepository.findById(dto.getApprovedUserId())
                .orElseThrow(() -> new NotFoundException("Approved user not found"));

        // Fetch current account
        CurrentAccount currentAcc = currentAccountRepository.findById(dto.getCurrentAccountId())
                .orElseThrow(()->new NotFoundException("Ma Tway Bu Kwa"));
        // Convert DTO to Entity
        SMELoan smeLoan = SMELoanMapper.toEntity(dto);
        smeLoan.setEntryUser(entryUser);
        smeLoan.setApprovedUser(approvedUser);
        smeLoan.setCurrentAccount(currentAcc);

        // Save entity
        smeLoan = smeLoanRepository.save(smeLoan);

        // Convert back to DTO and return
        return SMELoanMapper.toDTO(smeLoan);
    }
    @Transactional
    @Override
    public void approveSMELoan(Integer smeLoanId) {
        SMELoan smeLoan = smeLoanRepository.findById(smeLoanId)
                .orElseThrow(() -> new NotFoundException("SME Loan with ID " + smeLoanId + " not found."));
        if (smeLoan.getCurrentAccount() == null) {
            throw new NotFoundException("Current Account not found for SME Loan ID " + smeLoanId);
        }
        smeLoan.setStatus(LoanStatus.APPROVE); // Change status to "Approved"
        smeLoan.setApprovedDate(LocalDateTime.now()); // Set approved date to current date
        smeLoanRepository.save(smeLoan); // Save the updated loan
    }

}
