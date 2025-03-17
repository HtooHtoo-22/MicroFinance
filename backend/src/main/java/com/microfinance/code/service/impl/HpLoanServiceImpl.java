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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.microfinance.code.mapper.HPLoanMapper.toDTO;

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
       User entryUser = userRepository.findById(dto.getEntryUserId())
                .orElseThrow(() -> new NotFoundException("Entry user not found"));

        CurrentAccount currentAcc = currentAccountRepository.findByAccountId(dto.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("Current account not found"));

        if (!currentAcc.isFreezeStatus()) {
            throw new ValidationException("Cannot create HP loan: The associated current account (ID: " + currentAcc.getAccountId() + ") is frozen.");
        }

        HPLoan hpLoan = HPLoanMapper.toEntity(dto);
        hpLoan.setLoanId(HPLoanIDGenerator.generateLoanId());
        hpLoan.setEntryUser(entryUser);
        hpLoan.setCurrentAccount(currentAcc);
        BigDecimal interestRate  = rateRepo.findValueByRateType("HP Loan Interest Rate");
        hpLoan.setInterestRate(interestRate);

        if (dto.getTenor() <= 0) {
            throw new ValidationException("Tenor must be greater than 0");
        }
        hpLoan.setTenor(dto.getTenor()); // Ensure tenor is set
        hpLoan.setDuration(hpLoan.getTenor() * 12);
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
        return toDTO(hpLoan);
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
        if (hpLoan.getCurrentAccount().getAccountId() == null) {
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


    @Override
    public List<HPLoanDTO> getAllHPLoans() {
        List<HPLoan> loans = hpLoanRepo.findByStatus(LoanStatus.PENDING);
        List<HPLoanDTO> loanDTOs = new ArrayList<>();

        for (HPLoan loan : loans) {
            HPLoanDTO loanDTO = new HPLoanDTO();
            loanDTO.setId(loan.getId());
            loanDTO.setLoanId(loan.getLoanId());
            loanDTO.setLoanAmount(loan.getLoanAmount());
            loanDTO.setInterestRate(loan.getInterestRate());
            loanDTO.setGracePeriod(loan.getGracePeriod());
            loanDTO.setRegisteredDate(loan.getRegisteredDate().toString());
            loanDTO.setApprovedDate(loan.getApprovedDate() != null ? loan.getApprovedDate().toString() : null);
            loanDTO.setStatus(loan.getStatus());
            loanDTO.setEndDate(loan.getEndDate() != null ? loan.getEndDate().toString() : null);
            loanDTO.setDuration(loan.getDuration());
            loanDTO.setEntryUserId(loan.getEntryUser() != null ? loan.getEntryUser().getId() : null);
            loanDTO.setApprovedUserId(loan.getApprovedUser() != null ? loan.getApprovedUser().getId() : null);
            loanDTO.setCurrentAccountId(loan.getCurrentAccount().getAccountId() != null ? loan.getCurrentAccount().getAccountId() : null);
            loanDTO.setProductId(loan.getProduct() != null ? loan.getProduct().getId() : null);

            loanDTO.setCurrentCode(loan.getCurrentAccount().getAccountId());
            loanDTO.setProductName(loan.getProduct().getProductName()); // NEW
            loanDTO.setProductValue(loan.getProduct().getValue()); // NEW


            loanDTO.setDownPaymentRate(loan.getDownPaymentRate());
            loanDTO.setDealerCommissionRate(loan.getDealerCommissionRate());

            loanDTOs.add(loanDTO);
        }

        return loanDTOs;
    }

    @Override
    public HPLoanDTO getHPLoanById(Integer id) {
        HPLoan hpLoan = hpLoanRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("HP Loan not found with ID: " + id));
        return toDTO(hpLoan); // Use the mapper to convert to DTO
    }

    @Override
    public List<HPLoanDTO> getApprovedHPLoans() {
        List<HPLoan> approvedLoans = hpLoanRepo.findByStatus(LoanStatus.APPROVE);
        List<HPLoanDTO> loanDTOs = new ArrayList<>();

        for (HPLoan loan : approvedLoans) {
            HPLoanDTO loanDTO = new HPLoanDTO();
            loanDTO.setId(loan.getId());
            loanDTO.setLoanId(loan.getLoanId());
            loanDTO.setLoanAmount(loan.getLoanAmount());
            loanDTO.setInterestRate(loan.getInterestRate());
            loanDTO.setGracePeriod(loan.getGracePeriod());
            loanDTO.setRegisteredDate(loan.getRegisteredDate() != null ? loan.getRegisteredDate().toString() : null);
            loanDTO.setApprovedDate(loan.getApprovedDate() != null ? loan.getApprovedDate().toString() : null);
            loanDTO.setStatus(loan.getStatus());
            loanDTO.setEndDate(loan.getEndDate() != null ? loan.getEndDate().toString() : null);
            loanDTO.setDuration(loan.getDuration());
            loanDTO.setEntryUserId(loan.getEntryUser() != null ? loan.getEntryUser().getId() : null);
            loanDTO.setApprovedUserId(loan.getApprovedUser() != null ? loan.getApprovedUser().getId() : null);
            loanDTO.setCurrentAccountId(loan.getCurrentAccount() != null ? loan.getCurrentAccount().getAccountId() : null);
            loanDTO.setProductId(loan.getProduct() != null ? loan.getProduct().getId() : null);
            loanDTO.setDownPaymentRate(loan.getDownPaymentRate());
            loanDTO.setDealerCommissionRate(loan.getDealerCommissionRate());
            loanDTO.setCurrentCode(loan.getCurrentAccount() != null ? loan.getCurrentAccount().getAccountId() : null);
            loanDTO.setProductName(loan.getProduct() != null ? loan.getProduct().getProductName() : null);
            loanDTO.setProductValue(loan.getProduct() != null ? loan.getProduct().getValue() : null);
            loanDTO.setTenor(loan.getTenor());
            loanDTO.setEntryUserName(loan.getEntryUser() != null ? loan.getEntryUser().getName() : null); // Assuming User has a getName() method
            loanDTO.setApprovedUserName(loan.getApprovedUser() != null ? loan.getApprovedUser().getName() : null); // Assuming User has a getName() method
            loanDTO.setProductPhoto(loan.getProduct() != null ? loan.getProduct().getPhoto() : null); // Assuming Product has a getProductPhoto() method

            loanDTOs.add(loanDTO);
        }

        return loanDTOs;
    }

}
