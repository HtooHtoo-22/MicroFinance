package com.microfinance.code.service.impl;

import com.microfinance.code.mapper.CIFMapper;
import com.microfinance.code.repository.CIFRepo;
import com.microfinance.code.service.interFace.CIFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CIFServiceImpl implements CIFService {

    @Autowired
    private CIFMapper cifMapper;

    @Autowired
    private CIFRepo cifRepo;
    @Override
    public void hello() {
        System.out.println("Hello");
    }
}
