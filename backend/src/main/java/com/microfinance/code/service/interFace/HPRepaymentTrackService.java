package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.HPRepaymentTrackDTO;
import com.microfinance.code.dto.SMERepaymentTrackDTO;

import java.util.List;

public interface HPRepaymentTrackService {
    public List<HPRepaymentTrackDTO> getTrackListByLoanId(Integer loanId);
}
