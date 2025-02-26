package com.microfinance.code.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.exception.AlreadyExistException;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.CIFMapper;
import com.microfinance.code.model.Branch;
import com.microfinance.code.model.CIF;
import com.microfinance.code.model.User;
import com.microfinance.code.repository.BranchRepo;
import com.microfinance.code.repository.CIFRepo;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.CloudinaryService;
import com.microfinance.code.service.interFace.CIFService;
import com.microfinance.code.status.CIFStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CIFServiceImpl implements CIFService {  // Removed abstract


    @Autowired
    private CIFMapper cifMapper;

    @Autowired
    private CIFRepo cifRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private BranchRepo branchRepo;

    @Autowired
    private UserRepo userRepo;

    public CIFDTO createCIF(CIFDTO dto, MultipartFile frontNRC, MultipartFile backNRC, MultipartFile userPhoto) throws IOException {

        if (cifRepo.existsByNRC(dto.getNrc())) {
            throw new AlreadyExistException("NRC number already exists: " + dto.getNrc());
        }


        if (cifRepo.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistException("Email already exists: " + dto.getEmail());
        }

        Branch branch = branchRepo.findById(dto.getBranchId())
                .orElseThrow(() -> new NotFoundException("Branch not found with ID: " + dto.getBranchId()));

        // Fetch User from DB using userId
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + dto.getUserId()));

        // Generate CIF ID dynamically
        String cifId = generateCIFId(branch.getCode(), dto.getUserId());


        CIF cif = cifMapper.toEntity(dto);

        cif.setCifId(cifId);
        cif.setFrontNRCUrl(cloudinaryService.uploadFile(frontNRC));
        cif.setBackNRCUrl(cloudinaryService.uploadFile(backNRC));
        cif.setUserPhotoURL(cloudinaryService.uploadFile(userPhoto));


        CIF savedCif = cifRepo.save(cif);
        return cifMapper.toDTO(savedCif);
    }


    public List<CIFDTO> getAllCIFs() {
        return cifRepo.findAll()
                .stream()
                .map(cifMapper::toDTO)  // Convert entity to DTO
                .collect(Collectors.toList());
    }

    @Override
    public CIFDTO updateCIF(Integer id, Map<String, Object> updates) {
        Optional<CIF> optionalCIF = cifRepo.findById(id);
        if (optionalCIF.isEmpty()) {
            throw new NotFoundException("CIF not found with id: " + id);
        }

        CIF cif = optionalCIF.get();

        // Check if NRC is being updated and if it already exists in another CIF
        if (updates.containsKey("nrc")) {
            String newNRC = updates.get("nrc").toString();
            if (cifRepo.existsByNRCAndIdNot(newNRC, id)) {
                throw new AlreadyExistException("NRC number already exists: " + newNRC);
            }
        }

        // Check if Email is being updated and if it already exists in another CIF
        if (updates.containsKey("email")) {
            String newEmail = updates.get("email").toString();
            if (cifRepo.existsByEmailAndIdNot(newEmail, id)) {
                throw new AlreadyExistException("Email already exists: " + newEmail);
            }
        }

        if (updates.containsKey("incomeAmount")) {
            Object incomeValue = updates.get("incomeAmount");
            if (incomeValue instanceof Integer) {
                updates.put("incomeAmount", ((Integer) incomeValue).doubleValue()); // Convert Integer to Double
            }
        }

        if (updates.containsKey("status")) {
            String statusString = (String) updates.get("status");
            try {
                CIFStatus statusEnum = CIFStatus.valueOf(statusString.toUpperCase()); // Convert to Enum
                updates.put("status", statusEnum);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + statusString);
            }
        }


        // Loop through fields and update dynamically
        updates.forEach((key, value) -> {
            Field field = getField(CIF.class, key); // Custom method to get the field
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, cif, value);
            }
        });

        CIF updatedCif = cifRepo.save(cif);
        return cifMapper.toDTO(updatedCif);
    }


    private Field getField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }


    @Override
    public CIFDTO updateCIFStatus(Integer id, String status) {
        CIF cif = cifRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("CIF not found with id: " + id));

        cif.setStatus(CIFStatus.valueOf(status.toUpperCase()));
        cifRepo.save(cif);

        return cifMapper.toDTO(cif);
    }

    @Override
    public List<CIFDTO> getActiveCIFs() {
        List<CIF> activeCifs = cifRepo.findByStatus(CIFStatus.ACTIVE);
        return activeCifs.stream()
                .map(cifMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CIFDTO> getDeleteCIFs() {
        List<CIF> deleteCifs = cifRepo.findByStatus(CIFStatus.DELETE);
        return deleteCifs.stream()
                .map(cifMapper::toDTO)
                .collect(Collectors.toList());
    }
    private String generateCIFId(String branchCode, Integer userId) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Last 5 digits of timestamp
        return "CIF-" + branchCode + "-" + userId + "-" + timestamp;
    }
    @Override
    public CIFDTO getCifById(Integer id) {
        CIF cif = cifRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("CIF not found with id: " + id));

        return cifMapper.toDTO(cif); // ✅ Convert entity to DTO
    }
}
