package com.microfinance.code.service.impl;

import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.exception.AlreadyExistException;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.DealerMapper;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.DealerRepo;
import com.microfinance.code.service.interFace.DealerService;
import com.microfinance.code.status.DealerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DealerServiceImpl implements DealerService {

    @Autowired
    private DealerRepo dealerRepo;

    @Autowired
    private DealerMapper dealerMapper;

    @Autowired
    private CurrentAccountRepository currentAccountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public DealerDTO createDealer(DealerDTO dealerDTO) {
        // Check if email already exists
        Optional<Dealer> existingDealer = dealerRepo.findByEmail(dealerDTO.getEmail());
        if (existingDealer.isPresent()) {
            throw new AlreadyExistException("Dealer with this email already exists");
        }

        Dealer dealer = dealerMapper.toEntity(dealerDTO);
        dealer.setRegisterDate(LocalDate.now());
        dealer.setStatus(DealerStatus.ACTIVE); // Default status ACTIVE
        dealer.setPassword(passwordEncoder.encode(dealerDTO.getPassword())); // Hash password

        dealer.setCurrentAccount(currentAccountRepo.findById(dealerDTO.getCurrentAccountId())
                .orElseThrow(() -> new NotFoundException("Current Account not found")));

        Dealer savedDealer = dealerRepo.save(dealer);
        return dealerMapper.toDTO(savedDealer);
    }

    @Override
    public List<DealerDTO> getAllDealers() {
        List<Dealer> dealers = dealerRepo.findAll();
        return dealers.stream()
                .map(dealerMapper::toDTO)
                .collect(Collectors.toList());
    }
}
