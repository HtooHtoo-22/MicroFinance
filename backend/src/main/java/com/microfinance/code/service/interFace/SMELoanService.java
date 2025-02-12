package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.SMELoanDTO;

public interface SMELoanService {
    SMELoanDTO registerLoan(SMELoanDTO smeLoanDTO);
    SMELoanDTO approveLoan(Integer loanId, Integer approvedUserId);

    SMELoanDTO registerLoan(SMELoanDTO smeLoanDTO, int entryUserId, int currentAccountId);

    SMELoanDTO approveLoan(String loanId, int approvedUserId);
}

