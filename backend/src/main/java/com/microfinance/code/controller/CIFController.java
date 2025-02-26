package com.microfinance.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.CIFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cif")
public class CIFController {

    @Autowired
    private CIFService cifService;

    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CIFDTO> createCif(
            @RequestParam("cif") String cifJson,
            @RequestPart("frontNRC") MultipartFile frontNRC,
            @RequestPart("backNRC") MultipartFile backNRC,
            @RequestPart("userPhoto") MultipartFile userPhoto) {
        try {
            System.out.println("Received JSON: " + cifJson); // Debugging log
            System.out.println("Front Nrc: " + frontNRC);
            ObjectMapper objectMapper = new ObjectMapper();
            CIFDTO cifDTO = objectMapper.readValue(cifJson, CIFDTO.class);

            CIFDTO createdCif = cifService.createCIF(cifDTO, frontNRC, backNRC, userPhoto);
            return ApiResponse.success(HttpStatus.CREATED, 201, "CIF created successfully", createdCif);
        } catch (IOException e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Invalid JSON format: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<CIFDTO>> getAllCIFs() {
        List<CIFDTO> cifs = cifService.getAllCIFs();
        return ApiResponse.success(HttpStatus.OK, 200, "CIFs retrieved successfully", cifs);
    }

    @PatchMapping("/{id}")
    public ApiResponse<CIFDTO> updateCif(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        System.out.println("Received Updates: " + updates); // Log incoming data
        CIFDTO updatedCif = cifService.updateCIF(id, updates);
        return ApiResponse.success(HttpStatus.OK, 200, "CIF updated successfully", updatedCif);
    }


    @PatchMapping("/{id}/status")
    public ApiResponse<CIFDTO> updateCIFStatus(@PathVariable Integer id, @RequestParam String status) {
        CIFDTO updatedCif = cifService.updateCIFStatus(id, status);
        return ApiResponse.success(HttpStatus.OK, 200, "CIF status updated successfully", updatedCif);
    }

    @GetMapping("/active")
    public ApiResponse<List<CIFDTO>> getActiveCIFs() {
        List<CIFDTO> activeCifs = cifService.getActiveCIFs();
        return ApiResponse.success(HttpStatus.OK, 200, "Active CIFs retrieved successfully", activeCifs);
    }

    @GetMapping("/delete")
    public ApiResponse<List<CIFDTO>> getDeleteCIFs() {
        List<CIFDTO> deleteCifs = cifService.getDeleteCIFs();
        return ApiResponse.success(HttpStatus.OK, 200, "Delete CIFs retrieved successfully", deleteCifs);
    }
    @GetMapping("/{id}")
    public ApiResponse<CIFDTO> getCifById(@PathVariable Integer id) {
        CIFDTO cifDTO = cifService.getCifById(id);
        if (cifDTO == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "CIF not found for id " + id);
        }
        return ApiResponse.success(HttpStatus.OK, 200, "CIF found", cifDTO);
    }

}
