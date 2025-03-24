package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.MonthlySMELoanCountDTO;
import com.microfinance.code.dto.SMELateFeeSummaryDTO;
import com.microfinance.code.dto.SMELoanDTO;

import java.math.BigDecimal;
import java.util.List;

public interface SMELoanService {
    public SMELoanDTO createSMELoan(SMELoanDTO dto);
    public void approveSMELoan(Integer smeLoanId);
    public void rejectSMELoan(Integer smeLoanId);
    public void repayPrincipal(Integer smeLoanId , BigDecimal repaidPrincipal);

    public List<SMELoanDTO> getAllLoansByBranchId(Integer branchId);

    public SMELoanDTO getLoanById(Integer id);

    public SMELoanDTO getLoanByLoanId(String id);

    public SMELateFeeSummaryDTO getLateFeeAndODByLoanId(Integer loanId);
    List<SMELoanDTO> getAllSMELoans(); // Add this line

    List<MonthlySMELoanCountDTO> getApprovedLoansByBranchMonthly(Integer branchId);
}
