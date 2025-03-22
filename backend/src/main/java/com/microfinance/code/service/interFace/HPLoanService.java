package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.HPLateFeeSummaryDTO;
import com.microfinance.code.dto.HPLoanDTO;

import java.util.List;

public interface HPLoanService {
    HPLoanDTO createSMELoan(HPLoanDTO dto);

    public void rejectHPLoan(Integer loanId);

    public void approveHPLoan(Integer loanId,Integer approveUserId);
    public List<HPLoanDTO> getAllHPLoans();
    HPLoanDTO getHPLoanById(Integer id); // New method

    List<HPLoanDTO> getApprovedHPLoans();

    public HPLateFeeSummaryDTO getLateFeeAndODByLoanId(Integer loanId);
}
