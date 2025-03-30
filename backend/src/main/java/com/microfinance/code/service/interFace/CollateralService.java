package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.CollateralDTO;

import java.util.List;

public interface CollateralService {
    public CollateralDTO createCollateral(CollateralDTO dto);
    public List<CollateralDTO> getAllCollaterals();
    public CollateralDTO getCollateralById(Integer id);

    void deleteCollateral(Integer id);

    public List<CollateralDTO> getCollateralByAccId(String id);

    public List<CollateralDTO> getCollateralsByBranchId(Integer id);
}
