package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.HPLoanDTO;

public interface HPLoanService {
    HPLoanDTO createSMELoan(HPLoanDTO dto);

    public void rejectHPLoan(Integer loanId);

    public void approveHPLoan(Integer loanId,Integer approveUserId);
}
