package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.SMELoanDTO;

public interface SMELoanService {
    public SMELoanDTO createSMELoan(SMELoanDTO dto);
    public void approveSMELoan(Integer smeLoanId);
}
