package com.microfinance.code.service.impl;

import com.microfinance.code.dto.HPLoanDTO;
import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.etc.generator.HPLoanIDGenerator;
import com.microfinance.code.etc.generator.SMELoanIDGenerator;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.exception.ValidationException;
import com.microfinance.code.mapper.HPLoanMapper;
import com.microfinance.code.mapper.SMELoanMapper;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.interFace.HPLoanService;
import com.microfinance.code.service.interFace.HPScheduleService;
import com.microfinance.code.service.interFace.SMERepaymentScheduleService;
import com.microfinance.code.service.interFace.TransactionService;
import com.microfinance.code.status.LoanStatus;
import com.microfinance.code.status.transactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HpLoanServiceImpl implements HPLoanService {

    @Autowired
    private HPLoanRepo hpLoanRepo;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private CurrentAccountRepository currentAccountRepository;
    @Autowired
    private CollateralRepo collateralRepo;
    @Autowired
    private SMELoanHasCollateralRepo smeLoanHasCollateralRepo;
    @Autowired
    private RateRepository rateRepo;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private SMERepaymentScheduleService scheduleService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private HPScheduleService hpScheduleService;
    @Override
    public HPLoanDTO createSMELoan(HPLoanDTO dto) {
        // Fetch entry user
        User entryUser = userRepository.findById(dto.getEntryUserId())
                .orElseThrow(() -> new NotFoundException("Entry user not found"));

        // Fetch current account
        CurrentAccount currentAcc = currentAccountRepository.findById(dto.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("Current account not found"));

        // Convert DTO to Entity
        HPLoan hpLoan = HPLoanMapper.toEntity(dto);
        hpLoan.setLoanId(HPLoanIDGenerator.generateLoanId());
        hpLoan.setEntryUser(entryUser);
        hpLoan.setCurrentAccount(currentAcc);
        BigDecimal interestRate  = rateRepo.findValueByRateType("HP Loan Interest Rate");
        hpLoan.setInterestRate(interestRate);
        hpLoan.setDuration(hpLoan.getTenor()*12);
        // Initialize loan amount
        BigDecimal loanAmount = BigDecimal.ZERO;

        // Fetch product and ensure it's not null
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Ensure product value is not null before using it
        if (product.getValue() == null) {
            throw new NotFoundException("Product value is null");
        } else {
            System.out.println(product.getValue());
            loanAmount = product.getValue(); // Initialize loan amount
        }

        // Apply Down Payment Rate if exists
        if (hpLoan.getDownPaymentRate() != null) {
            // Ensure that the downPaymentRate is not null and calculate accordingly
            loanAmount = loanAmount.subtract(loanAmount.multiply(hpLoan.getDownPaymentRate()).divide(BigDecimal.valueOf(100)));
        }

        // Apply Dealer Commission Rate if exists
        if (hpLoan.getDealerCommissionRate() != null) {
            // Ensure that the dealerCommissionRate is not null and calculate accordingly
            loanAmount = loanAmount.add(loanAmount.multiply(hpLoan.getDealerCommissionRate()).divide(BigDecimal.valueOf(100)));
        }

        // Set the calculated loan amount
        hpLoan.setLoanAmount(loanAmount);

        // Save the loan in the repository
        hpLoan = hpLoanRepo.save(hpLoan);

        // Return DTO with the saved loan data
        return HPLoanMapper.toDTO(hpLoan);
    }

    @Override
    public void rejectHPLoan(Integer loanId) {
        HPLoan hpLoan = hpLoanRepo.findById(loanId)
                .orElseThrow(() -> new NotFoundException("HP Loan with ID " + loanId + " not found."));
        hpLoan.setStatus(LoanStatus.REJECT);
        // Save the SME Loan status update
        hpLoanRepo.save(hpLoan);
    }

    @Override
    public void approveHPLoan(Integer loanId,Integer approveUserId) {
        HPLoan hpLoan = hpLoanRepo.findById(loanId)
                .orElseThrow(() -> new NotFoundException("HP Loan with ID " + loanId + " not found."));
        if (hpLoan.getCurrentAccount() == null) {
            throw new NotFoundException("Current Account not found for HP Loan ID " + loanId);
        }
        hpLoan.setStatus(LoanStatus.APPROVE); // Change status to "Approved"
        //hpLoan.setPrincipal(hpLoan.getLoanAmount());
        hpLoan.setApprovedDate(LocalDateTime.now()); // Set approved date to current date
        User approveUser = userRepository.findById(approveUserId)
                .orElseThrow(() -> new NotFoundException("Approve user not found"));
        hpLoan.setApprovedUser(approveUser);
        hpLoanRepo.save(hpLoan); // Save the updated loan
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(transactionType.CR);
        transactionDTO.setAmount(hpLoan.getLoanAmount());
        transactionDTO.setCurrentAccountId(hpLoan.getProduct().getDealer().getCurrentAccount().getAccountId());
        transactionService.createTransaction(transactionDTO);
        hpScheduleService.createSchedule(hpLoan);
       // scheduleService.createSchedule(smeLoan);
    }
}
