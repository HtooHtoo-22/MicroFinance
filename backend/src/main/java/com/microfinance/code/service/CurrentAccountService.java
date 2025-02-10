package com.microfinance.code.service;

import com.microfinance.code.dto.CurrentAccountDTO;

public interface CurrentAccountService {
    CurrentAccountDTO createCurrentAccount(CurrentAccountDTO dto);
    CurrentAccountDTO getCurrentAccountById(String accountId);
}
