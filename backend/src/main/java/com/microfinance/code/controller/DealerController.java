package com.microfinance.code.controller;

import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.DealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dealers")
public class DealerController {

    @Autowired
    private DealerService dealerService;

    @PostMapping("/create")
    public ApiResponse<DealerDTO> createDealer(@RequestBody DealerDTO dealerDTO) {
        DealerDTO savedDealer = dealerService.createDealer(dealerDTO);
        return ApiResponse.success(HttpStatus.CREATED, 201, "Dealer created successfully", savedDealer);
    }

    @PutMapping("/{dealerId}/approve")
    public ApiResponse<DealerDTO> approveDealer(@PathVariable Integer dealerId) {
        DealerDTO updatedDealer = dealerService.approveDealer(dealerId);
        return ApiResponse.success(HttpStatus.OK, 200, "Dealer approved", updatedDealer);
    }

    @PutMapping("/{dealerId}/reject")
    public ApiResponse<DealerDTO> rejectDealer(@PathVariable Integer dealerId) {
        DealerDTO updatedDealer = dealerService.rejectDealer(dealerId);
        return ApiResponse.success(HttpStatus.OK, 200, "Dealer rejected", updatedDealer);
    }

    @GetMapping("/list")
    public ApiResponse<List<DealerDTO>> getAllDealers() {
        List<DealerDTO> dealers = dealerService.getAllDealers();
        return ApiResponse.success(HttpStatus.OK, 200, "Dealers retrieved successfully", dealers);
    }

    @GetMapping("/approved")
    public ApiResponse<List<DealerDTO>> getApprovedDealers() {
        List<DealerDTO> approvedDealers = dealerService.getApprovedDealers();
        return ApiResponse.success(HttpStatus.OK, 200, "Approved dealers retrieved successfully", approvedDealers);
    }
}