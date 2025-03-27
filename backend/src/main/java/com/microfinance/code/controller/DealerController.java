package com.microfinance.code.controller;

import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.dto.TransactionDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.DealerMapper;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.repository.DealerRepo;
import com.microfinance.code.service.interFace.DealerService;
import com.microfinance.code.service.interFace.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dealers")
public class DealerController {

    @Autowired
    private DealerService dealerService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private DealerRepo dealerRepo;

    @Autowired
    private DealerMapper dealerMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/create")
    public ApiResponse<DealerDTO> createDealer(@RequestBody DealerDTO dealerDTO) {
        DealerDTO savedDealer = dealerService.createDealer(dealerDTO);
        System.out.println(dealerDTO);
        // Send WebSocket notification
        messagingTemplate.convertAndSend("/topic/dealer-notifications", savedDealer);

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

//    @GetMapping("/{dealerId}/transactions")
//    public List<TransactionDTO> getTransactionsByCurrentAccountId(@PathVariable Integer dealerId) {
//        // Fetch the dealer by ID to get the current account ID
//        Dealer dealer = dealerRepo.findById(dealerId)
//                .orElseThrow(() -> new NotFoundException("Dealer not found"));
//
//        // Get the current account ID from the dealer
//        String currentAccountId = dealer.getCurrentAccount().getAccountId();
//
//        // Fetch transactions for the current account ID
//        return transactionService.getTransactionsByCurrentAccountId(currentAccountId);
//    }

    @GetMapping("/by-email/{email}")
    public ApiResponse<DealerDTO> getDealerByEmail(@PathVariable String email) {
        Dealer dealer = dealerService.findByEmail(email);
        DealerDTO dto = dealerMapper.toDTO(dealer);
        return ApiResponse.success(HttpStatus.OK, 200, "Dealer retrieved", dto);
    }

    @GetMapping("/{id}")
    public ApiResponse<DealerDTO> getDealerByID(@PathVariable Integer id) {
        DealerDTO dealerDTO = dealerService.getDealerById(id);
        return ApiResponse.success(HttpStatus.OK, 200, "Dealer retrieved successfully", dealerDTO);
    }
}