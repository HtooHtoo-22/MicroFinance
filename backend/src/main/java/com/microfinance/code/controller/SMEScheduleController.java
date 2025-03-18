package com.microfinance.code.controller;

import com.microfinance.code.dto.SMEScheduleDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.SMERepaymentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sme-schedules")
public class SMEScheduleController {

    @Autowired
    private SMERepaymentScheduleService scheduleService;

    @GetMapping("/scheduleListByLoanId/{loanId}")
    public ApiResponse<List<SMEScheduleDTO>> getSchedulesByLoanId(@PathVariable("loanId")Integer loanId){
        List<SMEScheduleDTO> schedules = scheduleService.getSchedulesByLoanId(loanId);
        return ApiResponse.success(HttpStatus.OK, 200, "Schedules retrieved successfully", schedules);
    }
}
