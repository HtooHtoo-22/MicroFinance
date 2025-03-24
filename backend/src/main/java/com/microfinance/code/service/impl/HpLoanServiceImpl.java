package com.microfinance.code.service.impl;

import com.microfinance.code.dto.*;
import com.microfinance.code.etc.generator.HPLoanIDGenerator;
import com.microfinance.code.etc.generator.SMELoanIDGenerator;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.exception.ValidationException;
import com.microfinance.code.mapper.HPLoanMapper;
import com.microfinance.code.mapper.SMELoanMapper;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.WebSocketNotificationService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.microfinance.code.mapper.HPLoanMapper.toDTO;

@Service
public class HpLoanServiceImpl implements HPLoanService {

    // Existing autowired dependencies remain the same
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
    @Autowired
    private WebSocketNotificationService notificationService;

    @Override
    @Transactional
    public HPLoanDTO createSMELoan(HPLoanDTO dto) {
        System.out.println("DTO : "+dto);
        // Validate input DTO
        if (dto == null) {
            throw new ValidationException("Loan DTO cannot be null");
        }

        User entryUser = userRepository.findById(dto.getEntryUserId())
                .orElseThrow(() -> new NotFoundException("Entry user not found with ID: " + dto.getEntryUserId()));

        CurrentAccount currentAcc = currentAccountRepository.findByAccountId(dto.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("Current account not found with ID: " + dto.getCurrentAccountId()));

        // Correct the freeze status check logic (assuming true means frozen)
        if (currentAcc.isFreezeStatus()) {
            throw new ValidationException("Cannot create HP loan: The associated current account (ID: " + currentAcc.getAccountId() + ") is frozen.");
        }

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + dto.getProductId()));

        HPLoan hpLoan = HPLoanMapper.toEntity(dto);
        hpLoan.setLoanId(HPLoanIDGenerator.generateLoanId());
        hpLoan.setEntryUser(entryUser);
        hpLoan.setCurrentAccount(currentAcc);
        hpLoan.setProduct(product);


        // Set interest rate
        BigDecimal interestRate = rateRepo.findValueByRateType("HP Loan Interest Rate");
        if (interestRate == null) {
            throw new ValidationException("HP Loan Interest Rate not found in rate repository");
        }
        hpLoan.setInterestRate(interestRate);

        // Validate and set tenor
        if (dto.getTenor() <= 0) {
            throw new ValidationException("Tenor must be greater than 0");
        }
        System.out.println("HPLoan Tenor Before Set : "+hpLoan.getTenor());

        hpLoan.setTenor(dto.getTenor());
        hpLoan.setDuration(dto.getTenor() * 12);

        // Calculate loan amount
        BigDecimal loanAmount = product.getValue() != null ? product.getValue() : BigDecimal.ZERO;

        if (dto.getDownPaymentRate() != null && dto.getDownPaymentRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal downPayment = loanAmount.multiply(dto.getDownPaymentRate()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            loanAmount = loanAmount.subtract(downPayment);
        }

        if (dto.getDealerCommissionRate() != null && dto.getDealerCommissionRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal commission = loanAmount.multiply(dto.getDealerCommissionRate()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            loanAmount = loanAmount.add(commission);
        }

        hpLoan.setLoanAmount(loanAmount);
        hpLoan.setStatus(LoanStatus.PENDING);
        hpLoan.setRegisteredDate(LocalDateTime.now());
        System.out.println("Lee Tenor : "+hpLoan.getTenor());
        HPLoan savedLoan = hpLoanRepo.save(hpLoan);
        System.out.println("Saved Loan : "+savedLoan);
        HPLoanDTO savedLoanDTO = HPLoanMapper.toDTO(savedLoan);
        notificationService.notifyNewHPLoan(savedLoanDTO);
        return savedLoanDTO;
    }

    @Override
    @Transactional
    public void rejectHPLoan(Integer loanId) {
        HPLoan hpLoan = hpLoanRepo.findById(loanId)
                .orElseThrow(() -> new NotFoundException("HP Loan with ID " + loanId + " not found"));

        if (hpLoan.getStatus() != LoanStatus.PENDING) {
            throw new ValidationException("Only pending loans can be rejected");
        }

        hpLoan.setStatus(LoanStatus.REJECT);
        hpLoanRepo.save(hpLoan);

        HPLoanDTO dto = HPLoanMapper.toDTO(hpLoan);
        notificationService.notifyHPLoanStatusChange(dto);
    }

    @Override
    @Transactional
    public void approveHPLoan(Integer loanId, Integer approveUserId) {
        System.out.println("Start");
        HPLoan hpLoan = hpLoanRepo.findById(loanId)
                .orElseThrow(() -> new NotFoundException("HP Loan with ID " + loanId + " not found"));

        if (hpLoan.getStatus() != LoanStatus.PENDING) {
            throw new ValidationException("Only pending loans can be approved");
        }

        User approveUser = userRepository.findById(approveUserId)
                .orElseThrow(() -> new NotFoundException("Approve user not found with ID: " + approveUserId));

        if (hpLoan.getCurrentAccount() == null || hpLoan.getCurrentAccount().getAccountId() == null) {
            throw new NotFoundException("Current Account not found for HP Loan ID " + loanId);
        }

        if (hpLoan.getProduct() == null || hpLoan.getProduct().getDealer() == null) {
            throw new NotFoundException("Product or Dealer information not found for HP Loan ID " + loanId);
        }

        hpLoan.setStatus(LoanStatus.APPROVE);
        hpLoan.setApprovedDate(LocalDateTime.now());
        hpLoan.setApprovedUser(approveUser);
        System.out.println("Before saving HP Loan");
        hpLoanRepo.save(hpLoan);
        System.out.println("After saving HP Loan");
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(transactionType.CR);
        transactionDTO.setAmount(hpLoan.getLoanAmount());
        transactionDTO.setCurrentAccountId(hpLoan.getProduct().getDealer().getCurrentAccount().getAccountId());
        transactionService.createTransaction(transactionDTO);
        System.out.println("Transaction complete and before schedule");
        hpScheduleService.createSchedule(hpLoan);
        System.out.println("After schedule");
        HPLoanDTO dto = HPLoanMapper.toDTO(hpLoan);
        notificationService.notifyHPLoanStatusChange(dto);
    }

    @Override
    public List<HPLoanDTO> getAllHPLoans() {
        List<HPLoan> loans = hpLoanRepo.findByStatus(LoanStatus.PENDING);
        return loans.stream()
                .map(HPLoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HPLoanDTO getHPLoanById(Integer id) {
        HPLoan hpLoan = hpLoanRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("HP Loan not found with ID: " + id));
        return HPLoanMapper.toDTO(hpLoan);
    }

    @Override
    public List<HPLoanDTO> getApprovedHPLoans() {
        List<HPLoan> approvedLoans = hpLoanRepo.findByStatus(LoanStatus.APPROVE);
        return approvedLoans.stream()
                .map(HPLoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Helper method (if needed elsewhere, could be moved to mapper)
    private HPLoanDTO convertToDTO(HPLoan hpLoan) {
        return HPLoanMapper.toDTO(hpLoan);
    }

    @Override
    public List<MonthlyHPLoanCountDTO> getApprovedLoansByBranchMonthly(Integer branchId) {
        List<HPLoan> loans = hpLoanRepo.findByEntryUser_Branch_Id(branchId);

        return loans.stream()
                .filter(loan -> loan.getStatus() == LoanStatus.APPROVE && loan.getApprovedDate() != null)
                .collect(Collectors.groupingBy(
                        loan -> loan.getApprovedDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new MonthlyHPLoanCountDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(MonthlyHPLoanCountDTO::getMonth))
                .collect(Collectors.toList());
    }

}