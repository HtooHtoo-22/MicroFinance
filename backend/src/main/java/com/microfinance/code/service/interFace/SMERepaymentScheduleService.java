package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.SMEScheduleDTO;
import com.microfinance.code.model.SMELoan;

import java.math.BigDecimal;
import java.util.List;

public interface SMERepaymentScheduleService {
    public void createSchedule(SMELoan smeLoan);
    public void editSchedule(SMELoan smeLoan, BigDecimal changedPrincipal);
    public List<SMEScheduleDTO> getSchedulesByLoanId(Integer loanId);
}
