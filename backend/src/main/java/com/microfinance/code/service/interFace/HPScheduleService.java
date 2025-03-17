package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.HPScheduleDTO;
import com.microfinance.code.model.HPLoan;

import java.util.List;

public interface HPScheduleService {
    public void createSchedule(HPLoan hpLoan);

    List<HPScheduleDTO> getSchedulesByLoanId(Integer loanId);
}
