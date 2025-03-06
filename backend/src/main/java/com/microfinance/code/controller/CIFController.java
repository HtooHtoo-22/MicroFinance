package com.microfinance.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.ProductDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.model.User;
import com.microfinance.code.repository.CIFRepo;
import com.microfinance.code.service.interFace.CIFService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cif")
@CrossOrigin(origins = "http://localhost:4200")
public class CIFController {

    @Autowired
    private CIFService cifService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CIFRepo cifRepo;
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CIFDTO> createCif(
            @RequestParam("userName") String userName,
            @RequestParam("gender") String gender,
            @RequestParam("job") String job,
            @RequestParam("incomeAmount") Double incomeAmount,
            @RequestParam("nrc") String nrc,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("state") String state,
            @RequestParam("township") String township,
            @RequestParam("address") String address,
            @RequestPart("frontNRC") MultipartFile frontNRC,
            @RequestPart("backNRC") MultipartFile backNRC,
            @RequestPart("userPhoto") MultipartFile userPhoto,
            Authentication authentication) {
        try {
            User user = (User ) authentication.getPrincipal(); // Assuming User is the principal

            // Create a new CIFDTO object
            CIFDTO cifDTO = new CIFDTO();
            cifDTO.setUserName(userName);
            cifDTO.setGender(gender);
            cifDTO.setJob(job);
            cifDTO.setIncomeAmount(incomeAmount);
            cifDTO.setNrc(nrc);
            cifDTO.setPhone(phone);
            cifDTO.setEmail(email);
            cifDTO.setState(state);
            cifDTO.setTownship(township);
            cifDTO.setAddress(address);

            // Call the service to create the CIF
            CIFDTO createdCif = cifService.createCIF(cifDTO, frontNRC, backNRC, userPhoto, user);
            return ApiResponse.success(HttpStatus.CREATED, 201, "CIF created successfully", createdCif);
        } catch (IOException e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Error processing files: " + e.getMessage());
        }
    }


    @GetMapping("/check-nrc")
    public ResponseEntity<Boolean> checkNRC(@RequestParam String nrc) {
        return ResponseEntity.ok(cifRepo.existsByNRC(nrc));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(cifRepo.existsByEmail(email));
    }


    @GetMapping("/list")
    public ApiResponse<List<CIFDTO>> getAllCIFs() {
        List<CIFDTO> cifs = cifService.getAllCIFs();
        return ApiResponse.success(HttpStatus.OK, 200, "CIFs retrieved successfully", cifs);
    }

    @PatchMapping("/{id}")
    public ApiResponse<CIFDTO> updateCif(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        CIFDTO updatedCif = cifService.updateCIF(id, updates);
        return ApiResponse.success(HttpStatus.OK, 200, "CIF updated successfully", updatedCif);
    }

    @GetMapping("/{id}")
    public ApiResponse<CIFDTO> getCifById(@PathVariable Integer id) {
        CIFDTO cifDTO = cifService.getCifById(id);
        if (cifDTO == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "CIF not found for id " + id);
        }
        return ApiResponse.success(HttpStatus.OK, 200, "CIF found", cifDTO);
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
}
