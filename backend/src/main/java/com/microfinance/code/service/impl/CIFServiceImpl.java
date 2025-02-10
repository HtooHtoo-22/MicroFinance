package com.microfinance.code.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.CIFMapper;
import com.microfinance.code.model.CIF;
import com.microfinance.code.repository.CIFRepo;
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

    public CIFDTO createCIF(CIFDTO dto, MultipartFile frontNRC, MultipartFile backNRC, MultipartFile userPhoto) throws IOException {
        CIF cif = cifMapper.toEntity(dto);
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

}
