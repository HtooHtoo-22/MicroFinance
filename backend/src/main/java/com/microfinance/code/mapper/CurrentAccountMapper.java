package com.microfinance.code.mapper;

import com.microfinance.code.dto.CurrentAccountDTO;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.CIF;

public class CurrentAccountMapper {

    public static CurrentAccount toEntity(CurrentAccountDTO dto) {
        CurrentAccount account = new CurrentAccount();
        account.setAccountId(dto.getAccountId());
        account.setMaxAmount(dto.getMaxAmount());
        account.setMinAmount(dto.getMinAmount());
        account.setCreatedDate(dto.getCreateDate());
        account.setTotalBalence(dto.getTotalBalance());
        account.setFreezeStatus(dto.isFreezeStatus());

        CIF cif = new CIF();
        cif.setId(dto.getCifId());
        account.setCif(cif);

        return account;
    }

    public static CurrentAccountDTO toDTO(CurrentAccount account) {
        CurrentAccountDTO dto = new CurrentAccountDTO();
        dto.setId(account.getId());
        dto.setAccountId(account.getAccountId());
        dto.setMaxAmount(account.getMaxAmount());
        dto.setMinAmount(account.getMinAmount());
        dto.setCreateDate(account.getCreatedDate());
        dto.setTotalBalance(account.getTotalBalence());
        dto.setFreezeStatus(account.isFreezeStatus());

        if (account.getCif() != null) {
            dto.setCifId(account.getCif().getId());
            dto.setUserName(account.getCif().getUserName()); // Assuming CIF has a userName field
        }

        return dto;
    }
}