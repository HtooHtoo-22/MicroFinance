package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.CurrentAccountDTO;

import java.util.List;

public interface CurrentAccountService {
    CurrentAccountDTO createCurrentAccount(CurrentAccountDTO dto);
    CurrentAccountDTO getCurrentAccountById(String accountId);
    List<CurrentAccountDTO> getAccountsByCifId(Integer cifId);
    CurrentAccountDTO updateCurrentAccount(String accountId, CurrentAccountDTO dto);
    List<CurrentAccountDTO> getAllCurrentACC();
    long countActiveAccounts();

    CurrentAccountDTO updateFreezeStatus(String accountId, boolean freeze);

    long getActiveCurrentAccountCountByBranch(Integer branchId);
}
