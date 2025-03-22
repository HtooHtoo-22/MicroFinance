package com.microfinance.code.service.impl;

import com.microfinance.code.dto.SMELateFeeSummaryDTO;
import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.dto.SMEScheduleDTO;
import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.etc.EmailSender;
import com.microfinance.code.etc.SmsSender;
import com.microfinance.code.etc.generator.SMELoanIDGenerator;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.exception.ValidationException;
import com.microfinance.code.mapper.SMELoanMapper;
import com.microfinance.code.mapper.SMEScheduleMapper;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.interFace.TransactionService;
import com.microfinance.code.service.interFace.SMELoanService;
import com.microfinance.code.service.interFace.SMERepaymentScheduleService;
import com.microfinance.code.status.LoanStatus;
import com.microfinance.code.status.RepaymentStatus;
import com.microfinance.code.status.transactionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
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
    private SMELoanMapper loanMapper;
    @Autowired
    private SMERepaymentScheduleRepo scheduleRepo;
    @Autowired
    private SMEScheduleMapper scheduleMapper;
    @Autowired
    private SMELateFeeCalculationRepo lateFeeRepo;
    @Autowired
    private SMELateFeeHoldingRepo lateFeeHoldingRepo;
    @Override
    public SMELoanDTO createSMELoan(SMELoanDTO dto) {
        // Fetch entry user
//        User entryUser = userRepository.findByUserId(dto.getEntryUserGenerateId())
//                .orElseThrow(() -> new NotFoundException("Entry user not found"));

        // Fetch approved user
        User entryUser = userRepository.findById(dto.getEntryUserId())
                .orElseThrow(() -> new NotFoundException("Entry user not found"));



// Fetch current account
        CurrentAccount currentAcc = currentAccountRepository.findByAccountId(dto.getCurrentAccountaccId()).
                orElseThrow(() -> new NotFoundException("Current account not found"));

        // Convert DTO to Entity
        SMELoan smeLoan = SMELoanMapper.toEntity(dto);
        smeLoan.setLoanId(SMELoanIDGenerator.generateLoanId());
        smeLoan.setEntryUser(entryUser);
        smeLoan.setCurrentAccount(currentAcc);

        // Calculate service charges
        smeLoan.setServiceCharge(dto.getServiceCharge());
        smeLoan.setInterestRate(dto.getInterestRate());

        // Validate collateral
        BigDecimal totalRemainingCollateralValue = calculateTotalRemainingCollateralValue(dto.getCollateralIds());
        if (totalRemainingCollateralValue.compareTo(smeLoan.getLoanAmount()) < 0) {
            throw new ValidationException("Your total collateral amount is not enough for your loan amount");
        }

        // Save SME Loan
        smeLoan = smeLoanRepository.save(smeLoan);

        // Save SME Loan Has Collateral records
        saveSMELoanHasCollateral(dto.getCollateralIds(), smeLoan);

        // Set the current account ID in DTO
        if (smeLoan.getCurrentAccount() != null) {
            dto.setCurrentAccountId(smeLoan.getCurrentAccount().getId());
        } else {
            throw new NotFoundException("Current account is missing in SME Loan");
        }

        return loanMapper.toDTO(smeLoan);
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
        smeLoan.setPrincipal(smeLoan.getLoanAmount());
        smeLoan.setApprovedDate(LocalDateTime.now()); // Set approved date to current date
        smeLoanRepository.save(smeLoan); // Save the updated loan
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(transactionType.CR);
        BigDecimal totalCharges = smeLoan.getDocumentFee().add(smeLoan.getServiceCharge());
        transactionDTO.setAmount(smeLoan.getLoanAmount().subtract(totalCharges));
        transactionDTO.setCurrentAccountId(smeLoan.getCurrentAccount().getAccountId());
        transactionService.createTransaction(transactionDTO);
        scheduleService.createSchedule(smeLoan);
        SmsSender.sendSms(smeLoan.getCurrentAccount().getCif().getPhone(),
                "RichCoin: Your SME loan of MMK "+smeLoan.getLoanAmount() +" has been approved. " +
                        "Please visit the "+smeLoan.getEntryUser().getBranch().getName()+" branch to proceed.");
        String email = smeLoan.getCurrentAccount().getCif().getEmail();  // Assuming email is stored in CIF
        String emailSubject = "Loan Approved - RichCoin";
        String emailBody = "Dear customer,\n\nYour SME loan of MMK " + smeLoan.getLoanAmount() +
                " has been approved. Please visit the " + smeLoan.getEntryUser().getBranch().getName() +
                " branch to proceed with the next steps.\n\nBest regards,\nRichCoin Team";

        // Call the sendEmail method
        boolean emailSent = EmailSender.sendEmail(email, emailSubject, emailBody);
    }
    @Override
    public void rejectSMELoan(Integer smeLoanId){
        SMELoan smeLoan = smeLoanRepository.findById(smeLoanId)
                .orElseThrow(() -> new NotFoundException("SME Loan with ID " + smeLoanId + " not found."));
        smeLoan.setStatus(LoanStatus.REJECT);
        // Find all collaterals linked to this SME Loan
        List<SMELoanHasCollateral> smeLoanHasCollaterals = smeLoanHasCollateralRepo.findBySmeLoan(smeLoan);

        // Delete all collaterals
        smeLoanHasCollateralRepo.deleteAll(smeLoanHasCollaterals);

        // Save the SME Loan status update
        smeLoanRepository.save(smeLoan);
    }
    @Override
    public void repayPrincipal(Integer smeLoanId, BigDecimal repaidPrincipal) {
        // Retrieve the SME loan by its ID
        SMELoan smeLoan = smeLoanRepository.findById(smeLoanId)
                .orElseThrow(() -> new RuntimeException("SME Loan not found"));


        // Subtract the repaid principal from the current principal
        BigDecimal currentPrincipal = smeLoan.getPrincipal();
        BigDecimal newPrincipal = currentPrincipal.subtract(repaidPrincipal);
        smeLoan.setPrincipal(newPrincipal);

        // Update the principal in the SME loan (uncomment if needed)
        // smeLoan.setPrincipal(newPrincipal);

        // Adjust the schedules
        scheduleService.editSchedule(smeLoan, newPrincipal);

        // Retrieve the current account associated with the SME loan
        CurrentAccount currentAccount = smeLoan.getCurrentAccount();

        // Create and save the transaction
        Transaction transaction = new Transaction();
        transaction.setType(transactionType.CR); // Ensure enum matches your transactionType
        transaction.setAmount(repaidPrincipal);
        transaction.setCurrentAccountId(currentAccount); // Links to the CurrentAccount
        transactionRepository.save(transaction);

        // Save the updated SME loan
        smeLoanRepository.save(smeLoan);
    }

    @Override
    public List<SMELoanDTO> getAllLoansByBranchId(Integer branchId) {
        List<SMELoan> loans = smeLoanRepository.findByEntryUser_Branch_Id(branchId);
        return loans.stream()
                .map(loanMapper::toDTO)
                .collect(Collectors.toList());

    }

    private BigDecimal calculateTotalRemainingCollateralValue(List<Integer> collateralIds) {
        BigDecimal totalRemainingCollateralValue = BigDecimal.ZERO;

        for (Integer collateralId : collateralIds) {
            Collateral collateral = collateralRepo.findById(collateralId)
                    .orElseThrow(() -> new NotFoundException("Collateral Id not found " + collateralId));

            // Check if the collateral is already used
            List<SMELoanHasCollateral> existingLoanCollaterals = smeLoanHasCollateralRepo.findByCollateral(collateral);
            if (existingLoanCollaterals != null) {
                BigDecimal totalUsedValue = smeLoanHasCollateralRepo.findTotalUsedValueByCollateralId(collateralId);
                totalRemainingCollateralValue = totalRemainingCollateralValue.add(
                        collateral.getValue().subtract(totalUsedValue != null ? totalUsedValue : BigDecimal.ZERO)
                );

            } else {
                totalRemainingCollateralValue = totalRemainingCollateralValue.add(collateral.getValue());
            }
        }

        return totalRemainingCollateralValue;
    }

    private void saveSMELoanHasCollateral(List<Integer> collateralIds, SMELoan smeLoan) {
        BigDecimal remainingLoanAmount = smeLoan.getLoanAmount();
        for (Integer collateralId : collateralIds) {
            Collateral collateral = collateralRepo.findById(collateralId)
                    .orElseThrow(() -> new NotFoundException("Collateral Id not found " + collateralId));

            // Calculate the used value for this collateral
            BigDecimal usedValue = collateral.getValue().min(remainingLoanAmount);

            // Reduce the remaining loan amount
            remainingLoanAmount = remainingLoanAmount.subtract(usedValue);

            // Create the SMELoanHasCollateral entity
            SMELoanHasCollateral loanHasCollateral = new SMELoanHasCollateral();
            loanHasCollateral.setCollateral(collateral);
            loanHasCollateral.setSmeLoan(smeLoan);
            loanHasCollateral.setUsedValue(usedValue); // Set the correct used value

            // Save the record
            smeLoanHasCollateralRepo.save(loanHasCollateral);
        }
    }
    @Override
    public SMELoanDTO getLoanById(Integer id){
        SMELoan loan = smeLoanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SME Loan  not found with ID: " + id));
        return loanMapper.toDTO(loan);
    }
    @Override
    public SMELoanDTO getLoanByLoanId(String id){
        SMELoan loan = smeLoanRepository.findByLoanId(id)
                .orElseThrow(() -> new NotFoundException("SME Loan  not found with Loan ID: " + id));
        return loanMapper.toDTO(loan);
    }
    @Override
    public SMELateFeeSummaryDTO getLateFeeAndODByLoanId(Integer loanId) {
        SMELateFeeSummaryDTO lateFeeSummaryDTO = new SMELateFeeSummaryDTO();

        // Fetch overdue repayment schedules
        List<SMERepaymentSchedule> schedules = scheduleRepo.findBySmeLoanIdAndStatusIn(loanId, List.of(RepaymentStatus.PARTIAL_OVERDUE, RepaymentStatus.FULL_OVERDUE));

        // If no overdue schedules are found, return an empty DTO or a flag indicating no data
        if (schedules.isEmpty()) {
            System.out.println("No overdue repayment schedules found for loan ID: " + loanId);
            return lateFeeSummaryDTO; // Return empty DTO or possibly a DTO with a "not found" flag
        }

        List<SMEScheduleDTO> odScheduleDTOs = schedules.stream()
                .map(scheduleMapper::toDTO)
                .collect(Collectors.toList());
        System.out.println("OD Schedules : " + odScheduleDTOs);
        lateFeeSummaryDTO.setOdSchedules(odScheduleDTOs);

        // Fetch late fee calculations
        List<SMELateFeeCalculation> calculations = lateFeeRepo.findBySmeLoanId(loanId);

        // If no late fee calculations are found, log and continue (or return an empty DTO)
        if (calculations.isEmpty()) {
            System.out.println("No late fee calculations found for loan ID: " + loanId);
            return lateFeeSummaryDTO; // Return empty DTO or a DTO with a "not found" flag
        }

        // Find max late days
        int maxLateDays = calculations.stream()
                .mapToInt(SMELateFeeCalculation::getLateDays)
                .max()
                .orElse(0);
        System.out.println("Late Days : " + maxLateDays);
        lateFeeSummaryDTO.setLateDays(maxLateDays);

        // Calculate total late fees
        BigDecimal totalLateFees = calculateTotalLateFees(calculations);
        System.out.println("Late Fees : " + totalLateFees);
        lateFeeSummaryDTO.setLateFees(totalLateFees);

        // Fetch late fee rate before 90 days
        BigDecimal rateBf90 = rateRepo.findValueByRateType("SME Late Fee Before 90 Days");
        if (rateBf90 == null) {
            System.out.println("Late fee rate before 90 days not found for loan ID: " + loanId);
            return lateFeeSummaryDTO; // Return empty DTO or a DTO with a "not found" flag
        }
        System.out.println("Before 90 Late Fee Rate : " + rateBf90);
        lateFeeSummaryDTO.setLateFeeRateBf90(rateBf90);

        // Fetch late fee rate after 90 days
        BigDecimal rateAf90 = rateRepo.findValueByRateType("SME Late Fee After 90 Days");
        if (rateAf90 == null) {
            System.out.println("Late fee rate after 90 days not found for loan ID: " + loanId);
            return lateFeeSummaryDTO; // Return empty DTO or a DTO with a "not found" flag
        }
        System.out.println("After 90 Late Fee Rate : " + rateAf90);
        lateFeeSummaryDTO.setLateFeeRateAf90(rateAf90);

        // Fetch late fee holding
        SMELateFeeHolding lateFeeHolding = fetchLateFeeHolding(loanId);
        if (lateFeeHolding == null) {
            System.out.println("No late fee holding found for loan ID: " + loanId);
            return lateFeeSummaryDTO; // Return empty DTO or a DTO with a "not found" flag
        }
        BigDecimal heldAmount = lateFeeHolding.getHoldAmount();
        System.out.println("Hold Amount : " + heldAmount);
        lateFeeSummaryDTO.setHoldAmount(heldAmount);

        // Fetch loan information to calculate outstanding amount
        Optional<SMELoan> optionalLoan = smeLoanRepository.findById(loanId);
        if (!optionalLoan.isPresent()) {
            System.out.println("Loan ID not found: " + loanId);
            return lateFeeSummaryDTO; // Return empty DTO or a DTO with a "not found" flag
        }
        BigDecimal outstandingAmount = calculateOutstandingAmount(optionalLoan.get());
        System.out.println("Outstanding Amount : " + outstandingAmount);
        lateFeeSummaryDTO.setOutStandingAmount(outstandingAmount);

        return lateFeeSummaryDTO;
    }


    private BigDecimal calculateTotalLateFees(List<SMELateFeeCalculation> lateFees) {
        return lateFees.stream()
                .map(SMELateFeeCalculation::getLateFees)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private SMELateFeeHolding fetchLateFeeHolding(Integer smeLoanId) {
        return lateFeeHoldingRepo.findBySmeLoan_Id(smeLoanId).orElse(null);
    }
    private BigDecimal calculateOutstandingAmount(SMELoan smeLoan) {
        BigDecimal outstandingAmount = BigDecimal.ZERO;

        // Fetch repayment schedules with required statuses
        List<SMERepaymentSchedule> repaymentSchedules = scheduleRepo.findBySmeLoanIdAndStatusIn(
                smeLoan.getId(), List.of(RepaymentStatus.NOT_DUE_YET, RepaymentStatus.PARTIAL_OVERDUE, RepaymentStatus.FULL_OVERDUE));

        if (repaymentSchedules.isEmpty()) {
            System.out.println("No repayment schedules found for the loan");
        }

        // Find the minimum principal amount
        BigDecimal minPrincipal = repaymentSchedules.stream()
                .map(SMERepaymentSchedule::getPrincipal)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        outstandingAmount = outstandingAmount.add(minPrincipal);
        BigDecimal totalOD = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (SMERepaymentSchedule schedule : repaymentSchedules) {
            BigDecimal interestAmount = schedule.getInterestAmount() != null ? schedule.getInterestAmount() : BigDecimal.ZERO;
            BigDecimal interestODAmount = schedule.getInterestODAmount() != null ? schedule.getInterestODAmount() : BigDecimal.ZERO;

            if (schedule.getStatus() == RepaymentStatus.NOT_DUE_YET) {
                outstandingAmount = outstandingAmount.add(interestAmount);
                totalInterest = totalInterest.add(interestAmount);
            }

            if (schedule.getStatus() == RepaymentStatus.FULL_OVERDUE || schedule.getStatus() == RepaymentStatus.PARTIAL_OVERDUE) {
                outstandingAmount = outstandingAmount.add(interestODAmount);
                totalOD = totalOD.add(interestODAmount);
                System.out.println("Schedule ID: " + schedule.getId() + " | OD Interest: " + interestODAmount);
            }
        }

        System.out.println("Principal: " + minPrincipal);
        System.out.println("Total OD Amount: " + totalOD);
        System.out.println("Total Remaining Interest Amount: " + totalInterest);

        return outstandingAmount;
    }
}