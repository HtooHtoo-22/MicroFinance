package com.microfinance.code.mapper;

import com.microfinance.code.dto.RateDTO;
import com.microfinance.code.model.Rate;

public class RateMapper {

    public static Rate toEntity(RateDTO dto) {
        Rate entity = new Rate();
        entity.setRateType(dto.getRateType());
        entity.setValue(dto.getValue());
        entity.setStatus(dto.isStatus());
        return entity;
    }

    public static RateDTO toDTO(Rate entity) {
        RateDTO dto = new RateDTO();
        dto.setRateType(entity.getRateType());
        dto.setValue(entity.getValue());
        dto.setStatus(entity.isStatus());
        return dto;
    }
}