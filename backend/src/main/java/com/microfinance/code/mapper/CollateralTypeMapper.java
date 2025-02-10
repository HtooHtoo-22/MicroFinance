package com.microfinance.code.mapper;

import com.microfinance.code.dto.CollateralTypeDTO;
import com.microfinance.code.model.CollateralType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CollateralTypeMapper {
    public CollateralTypeDTO toDTO(CollateralType collateralType) {
        if (collateralType == null) {
            return null;
        }
        CollateralTypeDTO dto = new CollateralTypeDTO();
        dto.setId(collateralType.getId());
        dto.setName(collateralType.getName());
        return dto;
    }
    public CollateralType toEntity(CollateralTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        CollateralType collateralType = new CollateralType();
        collateralType.setId(dto.getId());
        collateralType.setName(dto.getName());
        return collateralType;
    }
    public  List<CollateralTypeDTO> toDTOList(List<CollateralType> collateralTypes) {
        return collateralTypes.stream()
                .map(collateralType -> {
                    CollateralTypeDTO dto = new CollateralTypeDTO();
                    dto.setId(collateralType.getId());
                    dto.setName(collateralType.getName());
                    // Set other properties as needed
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
