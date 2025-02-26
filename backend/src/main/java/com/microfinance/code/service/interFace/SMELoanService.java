package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.SMELoanDTO;

import java.math.BigDecimal;

public interface SMELoanService {
    public SMELoanDTO createSMELoan(SMELoanDTO dto);
    public void approveSMELoan(Integer smeLoanId);
    public void rejectSMELoan(Integer smeLoanId);
    public void repayPrincipal(Integer smeLoanId , BigDecimal repaidPrincipal);
}
