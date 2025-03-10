package com.microfinance.code.service.impl;

import com.microfinance.code.dto.CollateralTypeDTO;
import com.microfinance.code.exception.AlreadyExistException;
import com.microfinance.code.exception.EmptyException;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.CollateralTypeMapper;
import com.microfinance.code.model.CollateralType;
import com.microfinance.code.repository.CollateralTypeRepo;
import com.microfinance.code.service.interFace.CollateralTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollateralTypeServiceImpl implements CollateralTypeService {

    @Autowired
    private CollateralTypeRepo collateralTypeRepo;

    @Autowired
    private CollateralTypeMapper collateralTypeMapper;

    public CollateralTypeDTO createCollateralType(CollateralTypeDTO dto) {
        if (collateralTypeRepo.existsByName(dto.getName())) {
            throw new AlreadyExistException("Collateral Type with name '" + dto.getName() + "' already exists.");
        }

        CollateralType collateralType = collateralTypeMapper.toEntity(dto);
        CollateralType savedCollateralType = collateralTypeRepo.save(collateralType);
        return collateralTypeMapper.toDTO(savedCollateralType);
    }
    public List<CollateralTypeDTO> getAllCollateralTypes() {
        List<CollateralType> collateralTypeList = collateralTypeRepo.findByStatus(false);
        return collateralTypeMapper.toDTOList(collateralTypeList);
    }

    public CollateralTypeDTO getCollateralTypeById(Integer id) {
        return collateralTypeRepo.findById(id)
                .map(collateralTypeMapper::toDTO)
                .orElseThrow(()->new NotFoundException("Collateral Type is not Found with this ID : "+id));
    }
    public CollateralTypeDTO updateCollateralType(Integer id, CollateralTypeDTO dto) {
        if (!collateralTypeRepo.existsById(id)) {
            return null;
        }
        CollateralType collateralType = collateralTypeMapper.toEntity(dto);
        collateralType.setId(id);
        CollateralType updatedCollateralType = collateralTypeRepo.save(collateralType);
        return collateralTypeMapper.toDTO(updatedCollateralType);
    }
    public boolean deleteCollateralType(Integer id) {
        // Check if the collateral type exists
        CollateralType collateralType = collateralTypeRepo.findById(id).orElse(null);
        if (collateralType == null) {
            return false;
        }
        // Set status to false (soft delete)
        collateralType.setStatus(true);
        collateralTypeRepo.save(collateralType);
        return true;
    }
}
