package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.DealerDTO;
import java.util.List;
import java.util.Map;

public interface DealerService {
    DealerDTO createDealer(DealerDTO dealerDTO);
    List<DealerDTO> getAllDealers();
//    DealerDTO updateDealer(Integer id, Map<String, Object> updates);
//    DealerDTO updateDealerStatus(Integer id, String status);
//    List<DealerDTO> getActiveDealers();
//    List<DealerDTO> getDeleteDealers();
    DealerDTO approveDealer(Integer dealerId);
    DealerDTO rejectDealer(Integer dealerId);
    List<DealerDTO> getApprovedDealers(); // Add this method

}
