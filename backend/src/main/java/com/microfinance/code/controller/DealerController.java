package com.microfinance.code.controller;

import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.DealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
