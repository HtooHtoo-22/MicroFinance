package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.SMERepaymentTrackDTO;

import java.util.List;

public interface SMERepaymentTrackService {
    public List<SMERepaymentTrackDTO> getTrackListByLoanId(Integer loanId);
}
