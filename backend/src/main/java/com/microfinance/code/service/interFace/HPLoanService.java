package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.HPLoanDTO;
import com.microfinance.code.dto.MonthlyHPLoanCountDTO;
import com.microfinance.code.dto.MonthlySMELoanCountDTO;

import java.util.List;

public interface HPLoanService {
    HPLoanDTO createSMELoan(HPLoanDTO dto);

    public void rejectHPLoan(Integer loanId);

    public void approveHPLoan(Integer loanId,Integer approveUserId);
    public List<HPLoanDTO> getAllHPLoans();
    HPLoanDTO getHPLoanById(Integer id); // New method

    List<HPLoanDTO> getApprovedHPLoans();
    List<MonthlyHPLoanCountDTO> getApprovedLoansByBranchMonthly(Integer branchId);
}
