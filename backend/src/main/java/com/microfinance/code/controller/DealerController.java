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

    @PostMapping
    public ApiResponse<DealerDTO> createDealer(@RequestBody DealerDTO dealerDTO) {
        DealerDTO savedDealer = dealerService.createDealer(dealerDTO);
        return ApiResponse.success(HttpStatus.CREATED, 201, "Dealer created successfully", savedDealer);
    }

    @GetMapping
    public ApiResponse<List<DealerDTO>> getAllDealers() {
        List<DealerDTO> dealers = dealerService.getAllDealers();
        return ApiResponse.success(HttpStatus.OK, 200, "Dealers retrieved successfully", dealers);
    }

    @PatchMapping("/{id}")  // Use PATCH for partial updates
    public ApiResponse<DealerDTO> updateDealer(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {

        DealerDTO updatedDealer = dealerService.updateDealer(id, updates);
        return ApiResponse.success(HttpStatus.OK, 200, "Dealer updated successfully", updatedDealer);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<DealerDTO> updateDealerStatus(@PathVariable Integer id, @RequestParam String status) {
        DealerDTO updatedDealer = dealerService.updateDealerStatus(id, status);
        return ApiResponse.success(HttpStatus.OK, 200, "Dealer status updated successfully", updatedDealer);
    }

    @GetMapping("/active")
    public ApiResponse<List<DealerDTO>> getActiveDealers() {
        List<DealerDTO> activeDealers = dealerService.getActiveDealers();
        return ApiResponse.success(HttpStatus.OK, 200, "Active Dealer retrieved successfully", activeDealers);
    }

    @GetMapping("/stop")
    public ApiResponse<List<DealerDTO>> getDeleteDealers() {
        List<DealerDTO> stopDalers = dealerService.getDeleteDealers();
        return ApiResponse.success(HttpStatus.OK, 200, "Stopping Dealer retrieved successfully", stopDalers);
    }

}
