package com.microfinance.code.service.impl;

import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.exception.AlreadyExistException;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.DealerMapper;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.DealerRepo;
import com.microfinance.code.repository.RoleRepository;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.interFace.DealerService;
import com.microfinance.code.status.DEALER;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealerServiceImpl implements DealerService {

    @Autowired
    private DealerRepo dealerRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DealerMapper dealerMapper;

    @Autowired
    private CurrentAccountRepository currentAccountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public DealerDTO createDealer(DealerDTO dealerDTO) {
        // Check if email exists
        if (dealerRepo.findByEmail(dealerDTO.getEmail()).isPresent()) {
            throw new AlreadyExistException("Dealer email already exists");
        }

        // Get current account by accountId string
        CurrentAccount currentAccount = currentAccountRepo.findByAccountId(dealerDTO.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("Current account not found with ID: " + dealerDTO.getCurrentAccountId()));
        CIF cif = currentAccount.getCif(); // Get CIF from CurrentAccount
        if (cif == null) {
                 throw new NotFoundException("CIF not found for current account");
        }

        Branch branch = cif.getBranch();
         if (branch == null) {
                 throw new IllegalStateException("CIF is not associated with any branch");
         }


        Dealer dealer = dealerMapper.toEntity(dealerDTO);
        dealer.setCurrentAccount(currentAccount);
        dealer.setRegisterDate(LocalDate.now());
        dealer.setStatusforDelar(DEALER.PENDING);

        Dealer savedDealer = dealerRepo.save(dealer);

        // Create user
        String cleanBusinessName = dealer.getBusinessName().toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");

        User user = new User();
        user.setUserId(generateUserId(currentAccount.getAccountId()));
        user.setName(dealer.getBusinessName());
        user.setEmail(dealer.getEmail());
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new AlreadyExistException(" email already exists");
        }
        user.setPassword(passwordEncoder.encode("dealer@richcoin"));
        user.setRole(roleRepository.findByRoleName("DEALER")
                .orElseThrow(() -> new NotFoundException("DEALER role not found")));
        user.setActive(false);
        user.setCreateDate(LocalDateTime.now());
        user.setBranch(branch);
        user.setDealer(dealer);

        userRepo.save(user);

        return dealerMapper.toDTO(savedDealer);
    }

    private String generateUserId(String accountId) {
        return "DLR-" + accountId + "-" + System.currentTimeMillis();
    }

// In DealerServiceImpl.java

    @Override
    @Transactional
    public DealerDTO approveDealer(Integer dealerId) {
        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new NotFoundException("Dealer not found"));

        dealer.setStatusforDelar(DEALER.ACTIVE);
        Dealer updatedDealer = dealerRepo.save(dealer);

        // Use the dealer's email directly
        String userEmail = dealer.getEmail();

        // Activate user using the correct email
        userRepo.findByEmail(userEmail)
                .ifPresent(user -> {
                    user.setActive(true);
                    userRepo.save(user);
                });

        return dealerMapper.toDTO(updatedDealer);
    }

    @Override
    @Transactional
    public DealerDTO rejectDealer(Integer dealerId) {
        Dealer dealer = dealerRepo.findById(dealerId)
                .orElseThrow(() -> new NotFoundException("Dealer not found"));

        dealer.setStatusforDelar(DEALER.REJECTED);
        Dealer updatedDealer = dealerRepo.save(dealer);

        // Use the dealer's email directly
        String userEmail = dealer.getEmail();

        // Deactivate user using the correct email
        userRepo.findByEmail(userEmail)
                .ifPresent(user -> {
                    user.setActive(false);
                    userRepo.save(user);
                });

        return dealerMapper.toDTO(updatedDealer);
    }

    // Helper method to generate user email (same as in createDealer)
    private String generateUserEmail(String businessName) {
        String cleanBusinessName = businessName.toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");
        return cleanBusinessName + "@richcoin.com";
    }

    @Override
    public List<DealerDTO> getAllDealers() {
        // Fetch only PENDING dealers
        List<Dealer> pendingDealers = dealerRepo.findByStatusforDelar(DEALER.PENDING);
        return pendingDealers.stream()
                .map(dealerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DealerDTO> getApprovedDealers() {
        List<Dealer> approvedDealers = dealerRepo.findByStatusforDelar(DEALER.ACTIVE); // Fetch only ACTIVE dealers
        return approvedDealers.stream()
                .map(dealerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Dealer findByEmail(String email) {
        return dealerRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Dealer not found with email: " + email));
    }
}
