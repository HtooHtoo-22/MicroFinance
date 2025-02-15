package com.microfinance.code.controller;

import com.microfinance.code.dto.RateDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rates")
public class RateController {

    @Autowired
    private RateService rateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RateDTO>>> getAllRates() {
        List<RateDTO> rates = rateService.getAllRates();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,200, "Rates retrieved successfully", rates));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RateDTO>> getRateById(@PathVariable Integer id) {
        try {
            RateDTO rate = rateService.getRateById(id);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,200, "Rate retrieved successfully", rate));
        } catch (Exception e) { // Catch potential exceptions
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND,404, "Rate not found"));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RateDTO>> createRate(@RequestBody RateDTO rateDTO) {
        RateDTO createdRate = rateService.createRate(rateDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED,201, "Rate created successfully", createdRate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RateDTO>> updateRate(@PathVariable Integer id, @RequestBody RateDTO rateDTO) {
        try {
            RateDTO updatedRate = rateService.updateRate(id, rateDTO);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,200, "Rate updated successfully", updatedRate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND,404, "Rate not found"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRate(@PathVariable Integer id) {
        rateService.deleteRate(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(HttpStatus.NO_CONTENT,204, "Rate deleted successfully"));
    }

    @GetMapping("/type/{rateType}")
    public ResponseEntity<ApiResponse<RateDTO>> getRateByType(@PathVariable String rateType) {
        try {
            RateDTO rate = rateService.getRateByType(rateType);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,200, "Rate retrieved successfully", rate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND,404, "Rate not found"));
        }
    }
}