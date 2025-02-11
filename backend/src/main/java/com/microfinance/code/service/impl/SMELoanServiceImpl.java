package com.microfinance.code.service.impl;

import com.microfinance.code.dto.SMELoanDTO;
import com.microfinance.code.repository.CurrentAccountRepository;
import com.microfinance.code.repository.SMELoanRepo;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.interFace.SMELoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMELoanServiceImpl implements SMELoanService {
    @Autowired
    private SMELoanRepo smeLoanRepository;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Override
    public SMELoanDTO createSMELoan(SMELoanDTO dto){

        return null;
    }


}
