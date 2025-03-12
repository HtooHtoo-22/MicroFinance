package com.microfinance.code.mapper;

import com.microfinance.code.dto.RateDTO;
import com.microfinance.code.model.Rate;
import org.springframework.stereotype.Component;

@Component // ✅ Make it a Spring-managed bean
public class RateMapper {

    // Convert Rate entity to RateDTO
    public RateDTO toDTO(Rate rate) {
        if (rate == null) {
            return null;
        }
        RateDTO dto = new RateDTO();
        dto.setId(rate.getId()); // ✅ Map the ID field
        dto.setRateType(rate.getRateType());
        dto.setValue(rate.getValue());
        dto.setStatus(rate.isStatus());
        return dto;
    }

    // Convert RateDTO to Rate entity
    public Rate toEntity(RateDTO dto) {
        if (dto == null) {
            return null;
        }
        Rate entity = new Rate();
        entity.setId(dto.getId()); // ✅ Map the ID field
        entity.setRateType(dto.getRateType());
        entity.setValue(dto.getValue());
        entity.setStatus(dto.isStatus());
        return entity;
    }
}