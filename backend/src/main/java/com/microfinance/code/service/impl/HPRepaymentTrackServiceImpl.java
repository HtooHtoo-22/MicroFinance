package com.microfinance.code.service.impl;

import com.microfinance.code.dto.HPRepaymentTrackDTO;
import com.microfinance.code.service.interFace.HPRepaymentTrackService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HPRepaymentTrackServiceImpl implements HPRepaymentTrackService {

    @Override
    public List<HPRepaymentTrackDTO> getTrackListByLoanId(Integer loanId){
        return null;
    }
}
