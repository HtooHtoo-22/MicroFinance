package com.microfinance.code.controller;

import com.microfinance.code.dto.SMEScheduleDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.SMERepaymentScheduleService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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


    @GetMapping("/loan/{smeLoanId}")
    public ResponseEntity<byte[]> generateLoanReport(@PathVariable Integer smeLoanId) {
        try {
            byte[] pdfBytes = scheduleService.generateReport(smeLoanId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=SMELoanRepaymentReport.pdf")
                    .body(pdfBytes);
        } catch (JRException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating report".getBytes());
        }
    }


}
