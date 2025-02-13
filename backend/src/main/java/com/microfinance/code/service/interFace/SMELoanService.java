package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.SMELoanDTO;
import jakarta.transaction.Transactional;

public interface SMELoanService {

    SMELoanDTO createSMELoan(SMELoanDTO dto);

    @Transactional
    void approveSMELoan(Integer smeLoanId);
}

