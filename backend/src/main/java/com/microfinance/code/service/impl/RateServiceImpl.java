package com.microfinance.code.service.impl;

import com.microfinance.code.dto.RateDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.Rate;
import com.microfinance.code.repository.RateRepository;
import com.microfinance.code.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RateServiceImpl implements RateService {

    @Autowired
    private RateRepository rateRepository;

    @Override
    public List<RateDTO> getAllRates() {
        List<Rate> rates = rateRepository.findAll();
        return rates.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public RateDTO getRateById(Integer id) {
        Rate rate = rateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rate not found with id: " + id));
        return convertToDTO(rate);
    }

    @Override
    public RateDTO createRate(RateDTO rateDTO) {
        Rate rate = convertToEntity(rateDTO);
        Rate savedRate = rateRepository.save(rate);
        return convertToDTO(savedRate);
    }

    @Override
    public RateDTO updateRate(Integer id, RateDTO rateDTO) {
        Rate rate = rateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rate not found with id: " + id));

        rate.setRateType(rateDTO.getRateType());
        rate.setValue(rateDTO.getValue());
        rate.setStatus(rateDTO.isStatus());

        Rate updatedRate = rateRepository.save(rate);
        return convertToDTO(updatedRate);
    }

    @Override
    public void deleteRate(Integer id) {
        rateRepository.deleteById(id);
    }

    @Override
    public RateDTO getRateByType(String rateType) {
        Rate rate = rateRepository.findByRateType(rateType);
        if (rate == null) {
            throw new NotFoundException("Rate not found with type: " + rateType);
        }
        return convertToDTO(rate);
    }

    private RateDTO convertToDTO(Rate rate) {
        RateDTO dto = new RateDTO();
        dto.setRateType(rate.getRateType());
        dto.setValue(rate.getValue());
        dto.setStatus(rate.isStatus());
        return dto;
    }

    private Rate convertToEntity(RateDTO dto) {
        Rate entity = new Rate();
        entity.setRateType(dto.getRateType());
        entity.setValue(dto.getValue());
        entity.setStatus(dto.isStatus());
        return entity;
    }
}