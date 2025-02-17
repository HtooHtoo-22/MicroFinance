package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.RateDTO;
import java.util.List;

public interface RateService {
    List<RateDTO> getAllRates();
    RateDTO getRateById(Integer id);
    RateDTO createRate(RateDTO rateDTO);
    RateDTO updateRate(Integer id, RateDTO rateDTO);
    void deleteRate(Integer id);
    RateDTO getRateByType(String rateType); //Example
}