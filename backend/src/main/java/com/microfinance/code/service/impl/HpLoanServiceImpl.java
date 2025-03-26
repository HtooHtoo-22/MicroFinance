package com.microfinance.code.service.impl;

import com.microfinance.code.dto.*;
import com.microfinance.code.etc.generator.HPLoanIDGenerator;
import com.microfinance.code.etc.generator.SMELoanIDGenerator;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.exception.ValidationException;
import com.microfinance.code.mapper.HPLoanMapper;
import com.microfinance.code.mapper.HPScheduleMapper;
import com.microfinance.code.mapper.SMELoanMapper;
import com.microfinance.code.mapper.SMEScheduleMapper;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.WebSocketNotificationService;
import com.microfinance.code.service.interFace.HPLoanService;
import com.microfinance.code.service.interFace.HPScheduleService;
import com.microfinance.code.service.interFace.SMERepaymentScheduleService;
import com.microfinance.code.service.interFace.TransactionService;
import com.microfinance.code.status.LoanStatus;
import com.microfinance.code.status.RepaymentStatus;
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
import java.util.Optional;
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

    @Autowired
    private HPScheduleRepo scheduleRepo;
    @Autowired
    private HPScheduleMapper scheduleMapper;
    @Autowired
    private HPLateFeeCalculationRepo lateFeeRepo;
    @Autowired
    private HPLateFeeHoldingRepo lateFeeHoldingRepo;

    private HPLoanMapper hpLoanMapper = new HPLoanMapper();

    @Override
    @Transactional
    public HPLoanDTO createSMELoan(HPLoanDTO dto) {
        System.out.println("DTO : " + dto);
        // Validate input DTO
        if (dto == null) {
            throw new ValidationException("Loan DTO cannot be null");
        }

        User entryUser  = userRepository.findById(dto.getEntryUserId())
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
        hpLoan.setEntryUser (entryUser );
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
        hpLoan.setTenor(dto.getTenor()); // Ensure this line is uncommented
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
        HPLoan savedLoan = hpLoanRepo.save(hpLoan);
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
        hpLoanRepo.save(hpLoan);
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(transactionType.CR);
        transactionDTO.setAmount(hpLoan.getLoanAmount());
        transactionDTO.setCurrentAccountId(hpLoan.getProduct().getDealer().getCurrentAccount().getAccountId());
        transactionService.createTransaction(transactionDTO);
        hpScheduleService.createSchedule(hpLoan);
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
                .map(loan -> {
                    // Check the loan status for each loan
                    String loanStatus = getLoanStatus(loan); // Get the loan status based on repayment schedules
                    HPLoanDTO loanDTO = hpLoanMapper.toDTO(loan); // Map to DTO
                    loanDTO.setLoanStatus(loanStatus); // Set the loan status in the DTO
                    return loanDTO;
                })
                .collect(Collectors.toList( ));
    }


    private String getLoanStatus(HPLoan loan) {
        // Fetch all repayment schedules for the loan
        List<HPSchedule> repaymentSchedules = scheduleRepo.findByHpLoanId(loan.getId());
        // Handle case where there are no repayment schedules
        if (repaymentSchedules == null || repaymentSchedules.isEmpty()) {
            // Log an error or return a default loan status if no repayment schedules exist
            return "No Repayment Schedules";
        }

        // Fetch all late fee calculations for the loan
        List<HPLateFeeCalculation> lateFees = lateFeeRepo.findByHpLoanId(loan.getId());


        // Additional loan status checks
        boolean isPaid = true;
        boolean isHealthy = true;

        // Check the repayment schedule statuses
        for (HPSchedule schedule : repaymentSchedules) {
            if (schedule.getStatus() == RepaymentStatus.ALL_PAID) {
                continue; // Skip PAID schedules for Paid Loan check
            } else if (schedule.getStatus() == RepaymentStatus.INTEREST_OD_PRINCIPAL_OD || schedule.getStatus() == RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD) {
                isHealthy = false;
                isPaid = false;// If any schedule is overdue, the loan is not healthy
            } else  {
                isPaid = false; // If any schedule is not PAID, the loan is not fully paid
            }
        }

        // Determine the loan status
        if (isPaid) {
            return "Paid Loan"; // All schedules are PAID
        } else if (isHealthy) {
            return "Healthy Loan"; // No overdue schedules
        } else {
            if (lateFees == null || lateFees.isEmpty()) {
                return "Watchlist Loan";
            }
            int maxLateDays = lateFees.stream()
                    .mapToInt(HPLateFeeCalculation::getLateDays)
                    .max()
                    .orElse(0); // Default to 0 if there are no late fees

            // Check for Watchlist or NPL status based on maxLateDays
            if (maxLateDays >= 90) {
                return "NPL Loan"; // If max late days are 90 or more, it's an NPL loan
            } else if (maxLateDays > 0) {
                return "Watchlist Loan"; // If max late days are less than 90 but greater than 0, it's a Watchlist loan
            }else{
                return "gg";
            }
        }
    }
    @Override
    public HPLateFeeSummaryDTO getLateFeeAndODByLoanId(Integer loanId) {
        HPLateFeeSummaryDTO lateFeeSummaryDTO = new HPLateFeeSummaryDTO();
        List<HPSchedule> schedules = scheduleRepo.findByHPLoanIdAndStatusIn(loanId, List.of(RepaymentStatus.INTEREST_OD_PRINCIPAL_OD,
                RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD));
        if (schedules.isEmpty()) {
            System.out.println("No overdue repayment schedules found for loan ID: " + loanId);
            return null;
        }
        List<HPScheduleDTO> odScheduleDTOs = schedules.stream()
                .map(scheduleMapper::toDTO)
                .collect(Collectors.toList());
        System.out.println("OD Schedules : " + odScheduleDTOs);
        lateFeeSummaryDTO.setOdSchedules(odScheduleDTOs);

        List<HPLateFeeCalculation> calculations = lateFeeRepo.findByHpLoanId(loanId);
        int maxLateDays = calculations.stream()
                .mapToInt(HPLateFeeCalculation::getLateDays)
                .max()
                .orElse(0);
        System.out.println("Late Days : " + maxLateDays);
        lateFeeSummaryDTO.setLateDays(maxLateDays);

        BigDecimal interestLateFees = calculateInterestLateFees(calculations);
        System.out.println("Interest Late Fees : " + interestLateFees);
        lateFeeSummaryDTO.setInterestLateFees(interestLateFees);

        BigDecimal principalLateFees = calculatePrincipalLateFees(calculations);
        System.out.println("Principal Late Fees : " + principalLateFees);
        lateFeeSummaryDTO.setPrincipalLateFees(principalLateFees);

        BigDecimal rateBf90 = rateRepo.findValueByRateType("HP Late Fee Before 90 Days");
        System.out.println("Before 90 Late Fee Rate : " + rateBf90);
        lateFeeSummaryDTO.setLateFeeRateBf90(rateBf90);

        BigDecimal rateAf90 = rateRepo.findValueByRateType("HP Late Fee After 90 Days");
        System.out.println("After 90 Late Fee Rate : " + rateAf90);
        lateFeeSummaryDTO.setLateFeeRateAf90(rateAf90);

        HPLateFeeHolding lateFeeHolding = fetchLateFeeHolding(loanId);
        BigDecimal heldAmount = BigDecimal.ZERO;  // Default to zero if lateFeeHolding is null

        if (lateFeeHolding != null) {
            heldAmount = lateFeeHolding.getHoldAmount();
            System.out.println("Hold Amount : " + heldAmount);
        } else {
            System.out.println("No late fee holding found for loan ID: " + loanId);
        }

        lateFeeSummaryDTO.setHoldAmount(heldAmount);


        Optional<HPLoan> optionalLoan = hpLoanRepo.findById(loanId);
        BigDecimal outstandingAmount = calculateOutstandingAmount(optionalLoan.get());
        System.out.println("Outstanding Amount : " + outstandingAmount);
        lateFeeSummaryDTO.setOutStandingAmount(outstandingAmount);

        return lateFeeSummaryDTO;
    }

    private BigDecimal calculateInterestLateFees(List<HPLateFeeCalculation> lateFees) {
        return lateFees.stream()
                .map(HPLateFeeCalculation::getInterestLateFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private BigDecimal calculatePrincipalLateFees(List<HPLateFeeCalculation> lateFees) {
        return lateFees.stream()
                .map(HPLateFeeCalculation::getPrincipalLateFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private HPLateFeeHolding fetchLateFeeHolding(Integer smeLoanId) {
        return lateFeeHoldingRepo.findByHpLoan_Id(smeLoanId).orElse(null);
    }
    private BigDecimal calculateOutstandingAmount(HPLoan hpLoan) {
        // Fetch repayment schedules with required statuses
        List<HPSchedule> repaymentSchedules = scheduleRepo.findByHPLoanIdAndStatusIn(
                hpLoan.getId(), List.of(RepaymentStatus.NOT_DUE_YET, RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD, RepaymentStatus.INTEREST_OD_PRINCIPAL_OD));
        System.out.println("Repayment Schedules List : " + repaymentSchedules);
        if (repaymentSchedules.isEmpty()) {
            throw new RuntimeException("No repayment schedules found for the loan");
        }

        BigDecimal totalInterestOD = BigDecimal.ZERO;
        BigDecimal totalInstallment = BigDecimal.ZERO;
        BigDecimal totalPrincipalOD = BigDecimal.ZERO;

        BigDecimal outstandingAmount = BigDecimal.ZERO;

        for (HPSchedule schedule : repaymentSchedules) {
            BigDecimal interestAmount = schedule.getInterestAmount() != null ? schedule.getInterestAmount() : BigDecimal.ZERO;
            BigDecimal interestODAmount = schedule.getInterestODAmount() != null ? schedule.getInterestODAmount() : BigDecimal.ZERO;

            BigDecimal principalAmount = schedule.getPrincipal() != null ? schedule.getPrincipal() : BigDecimal.ZERO;
            BigDecimal principalODAmount = schedule.getPrincipalODAmount() != null ? schedule.getPrincipalODAmount() : BigDecimal.ZERO;

            if (schedule.getStatus() == RepaymentStatus.NOT_DUE_YET) {
                // Add the entire installment amount (principal + interest) to the outstanding amount
                BigDecimal installmentAmount = principalAmount.add(interestAmount);
                outstandingAmount = outstandingAmount.add(installmentAmount);

                // Add the entire installment (principal + interest) to the total installment
                totalInstallment = totalInstallment.add(installmentAmount);
            }


            if (schedule.getStatus() == RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD || schedule.getStatus() == RepaymentStatus.INTEREST_OD_PRINCIPAL_OD) {
                // Add the overdue interest and principal to the outstanding amount
                outstandingAmount = outstandingAmount.add(interestODAmount).add(principalODAmount);
                // Accumulate the total overdue interest and principal
                totalInterestOD = totalInterestOD.add(interestODAmount);
                totalPrincipalOD = totalPrincipalOD.add(principalODAmount);
            }
        }

        System.out.println("Total Interest OD Amount: " + totalInterestOD);
        System.out.println("Total Principal OD Amount: " + totalPrincipalOD);
        System.out.println("Total Remaining Installment: " + totalInstallment);
        System.out.println("OOOOOO "+outstandingAmount);
        return outstandingAmount;
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

