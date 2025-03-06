package com.microfinance.code.service.interFace;

import com.microfinance.code.model.HPLoan;
import jakarta.persistence.Column;

public interface HPScheduleService {
    public void createSchedule(HPLoan hpLoan);
}
