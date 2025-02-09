package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.CollateralTypeDTO;

import java.util.List;

public interface CollateralTypeService {
    public CollateralTypeDTO createCollateralType(CollateralTypeDTO dto);
    public CollateralTypeDTO getCollateralTypeById(Integer id);
    public CollateralTypeDTO updateCollateralType(Integer id, CollateralTypeDTO dto);
    public boolean deleteCollateralType(Integer id);
    public List<CollateralTypeDTO> getAllCollateralTypes();
}
