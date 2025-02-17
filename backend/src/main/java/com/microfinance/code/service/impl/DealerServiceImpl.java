package com.microfinance.code.service.impl;

import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.exception.AlreadyExistException;
import com.microfinance.code.exception.BadRequestException;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.DealerMapper;
import com.microfinance.code.model.CIF;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.DealerRepo;
import com.microfinance.code.service.interFace.DealerService;
import com.microfinance.code.status.CIFStatus;
import com.microfinance.code.status.DealerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.util.FieldUtils.getField;

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
        Optional<Dealer> existingDealerByEmail = dealerRepo.findByEmail(dealerDTO.getEmail());
        if (existingDealerByEmail.isPresent()) {
            throw new AlreadyExistException("Dealer with this email already exists");
        }

        Optional<Dealer> existingDealerByBussinessName = dealerRepo.findByBusinessName(dealerDTO.getBusinessName());
        if (existingDealerByBussinessName.isPresent()) {
            throw new AlreadyExistException("Dealer with this bussiness name already exists");
        }

        Optional<Dealer> existingDealerByCurrentAccountId = dealerRepo.findByCurrentAccountId(dealerDTO.getCurrentAccountId());
        if (existingDealerByCurrentAccountId.isPresent()) {
            throw new AlreadyExistException("Dealer with this cureentAccount already exists");
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

    @Override
    public DealerDTO updateDealer(Integer id, Map<String, Object> updates) {
        Dealer dealer = dealerRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Dealer not found"));


        if (updates.containsKey("oldPassword") && updates.containsKey("newPassword")) {
            String oldPassword = updates.get("oldPassword").toString();
            String newPassword = updates.get("newPassword").toString();


            if (!passwordEncoder.matches(oldPassword, dealer.getPassword())) {
                throw new BadRequestException("Old password is incorrect!");
            }


            dealer.setPassword(passwordEncoder.encode(newPassword));


            updates.remove("oldPassword");
            updates.remove("newPassword");
        }

        if (updates.containsKey("email")) {
            String newEmail = updates.get("email").toString();
            Optional<Dealer> existingDealerByEmail = dealerRepo.findByEmail(newEmail);
            if (existingDealerByEmail.isPresent() && !existingDealerByEmail.get().getId().equals(dealer.getId())) {
                throw new AlreadyExistException("Dealer with this email already exists");
            }
        }


        if (updates.containsKey("businessName")) {
            String newBusinessName = updates.get("businessName").toString();
            Optional<Dealer> existingDealerByBusinessName = dealerRepo.findByBusinessName(newBusinessName);
            if (existingDealerByBusinessName.isPresent() && !existingDealerByBusinessName.get().getId().equals(dealer.getId())) {
                throw new AlreadyExistException("Dealer with this business name already exists");
            }
        }

        updates.forEach((key, value) -> {
            Field field = getField(Dealer.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, dealer, value);
            }
        });



        Dealer updatedDealer = dealerRepo.save(dealer);
        return dealerMapper.toDTO(updatedDealer);
    }

    @Override
    public DealerDTO updateDealerStatus(Integer id, String status) {
        Dealer dealer = dealerRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Dealer not found with id: " + id));

        // Use the safe enum conversion method
        dealer.setStatus(DealerStatus.fromString(status));

        dealerRepo.save(dealer);
        return dealerMapper.toDTO(dealer);
    }


    @Override
    public List<DealerDTO> getActiveDealers() {
        List<Dealer> activeDealers = dealerRepo.findByStatus(DealerStatus.ACTIVE.ACTIVE);
        return activeDealers.stream()
                .map(dealerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DealerDTO> getDeleteDealers() {
        List<Dealer> deleteDealers = dealerRepo.findByStatus(DealerStatus.ACTIVE.STOP);
        return deleteDealers.stream()
                .map(dealerMapper::toDTO)
                .collect(Collectors.toList());
    }

}
