package com.microfinance.code.controller;

import com.microfinance.code.dto.LoanDashboardDTO;
import com.microfinance.code.service.interFace.LoanDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class LoanDashboardController {

    private final LoanDashboardService loanDashboardService;

    @GetMapping("/loan-metrics")
    public LoanDashboardDTO getLoanMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to current month if no dates provided
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return loanDashboardService.getLoanDashboardMetrics(startDate, endDate);
    }
}