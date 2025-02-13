package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.DealerDTO;
import java.util.List;

public interface DealerService {
    DealerDTO createDealer(DealerDTO dealerDTO);
    List<DealerDTO> getAllDealers();
}
