package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.CurrentAccountDTO;

import java.util.List;

public interface CurrentAccountService {
    CurrentAccountDTO createCurrentAccount(CurrentAccountDTO dto);
    CurrentAccountDTO getCurrentAccountById(String accountId);
    List<CurrentAccountDTO> getAllCurrentACC();
    long getCurrentAccountCountByBranch(Integer branchId);
    CurrentAccountDTO updateCurrentAccount(String accountId, CurrentAccountDTO dto);
    CurrentAccountDTO updateFreezeStatus(String accountId, boolean freeze);
}
