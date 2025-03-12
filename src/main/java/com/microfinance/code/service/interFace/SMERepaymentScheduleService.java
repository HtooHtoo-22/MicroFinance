package com.microfinance.code.service.interFace;

import com.microfinance.code.model.SMELoan;

import java.math.BigDecimal;

public interface SMERepaymentScheduleService {
    public void createSchedule(SMELoan smeLoan);
    public void editSchedule(SMELoan smeLoan, BigDecimal changedPrincipal);
}
