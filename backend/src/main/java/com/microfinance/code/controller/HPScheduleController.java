package com.microfinance.code.controller;

import com.microfinance.code.dto.HPScheduleDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.mapper.HPScheduleMapper;
import com.microfinance.code.model.HPSchedule;
import com.microfinance.code.service.interFace.HPScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hp-loans-schedule")
public class HPScheduleController {

    @Autowired
    private HPScheduleService hpScheduleService;

    @Autowired
    private HPScheduleMapper hpScheduleMapper;

    @GetMapping("/{LoanId}/schedules")
    public ApiResponse<List<HPScheduleDTO>> getLoanSchedules(@PathVariable("LoanId") Integer id) {
        List<HPScheduleDTO> schedules = hpScheduleService.getSchedulesByLoanId(id);
        return ApiResponse.success(HttpStatus.OK, HttpStatus.OK.value(), "Successfully fetched schedules for HP loan ID: " + id, schedules);
    }
}