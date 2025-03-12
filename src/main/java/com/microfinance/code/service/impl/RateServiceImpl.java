package com.microfinance.code.service.impl;

import com.microfinance.code.dto.RateDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.RateMapper; // ✅ Import the mapper
import com.microfinance.code.model.Rate;
import com.microfinance.code.repository.RateRepository;
import com.microfinance.code.service.interFace.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RateServiceImpl implements RateService {

    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private RateMapper rateMapper; // ✅ Inject the mapper

    @Override
    public List<RateDTO> getAllRates() {
        List<Rate> rates = rateRepository.findAll();
        return rates.stream()
                .map(rateMapper::toDTO) // ✅ Use the mapper
                .collect(Collectors.toList());
    }

    @Override
    public RateDTO getRateById(Integer id) {
        Rate rate = rateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rate not found with id: " + id));
        return rateMapper.toDTO(rate); // ✅ Use the mapper
    }

    @Override
    public RateDTO createRate(RateDTO rateDTO) {
        Rate rate = rateMapper.toEntity(rateDTO); // ✅ Use the mapper
        Rate savedRate = rateRepository.save(rate);
        return rateMapper.toDTO(savedRate); // ✅ Use the mapper
    }

    @Override
    public RateDTO updateRate(Integer id, RateDTO rateDTO) {
        Rate rate = rateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rate not found with id: " + id));

        // Update fields
        rate.setRateType(rateDTO.getRateType());
        rate.setValue(rateDTO.getValue());
        rate.setStatus(rateDTO.isStatus());

        Rate updatedRate = rateRepository.save(rate);
        return rateMapper.toDTO(updatedRate); // ✅ Use the mapper
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
        return rateMapper.toDTO(rate); // ✅ Use the mapper
    }
}