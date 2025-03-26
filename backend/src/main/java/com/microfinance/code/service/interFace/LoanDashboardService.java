package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.LoanDashboardDTO;
import java.time.LocalDate;

public interface LoanDashboardService {
    LoanDashboardDTO getLoanDashboardMetrics(LocalDate startDate, LocalDate endDate);
}