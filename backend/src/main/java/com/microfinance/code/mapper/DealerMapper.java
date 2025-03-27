package com.microfinance.code.mapper;

import com.microfinance.code.dto.CurrentAccountDTO;
import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.Dealer;
import org.springframework.stereotype.Component;

@Component
public class DealerMapper {
    public Dealer toEntity(DealerDTO dto) {
        Dealer dealer = new Dealer();
        dealer.setId(dto.getId());
        dealer.setBusinessName(dto.getBusinessName());
        dealer.setAddress(dto.getAddress());
        dealer.setPhone(dto.getPhone());
        dealer.setEmail(dto.getEmail());
        dealer.setCompanyValue(dto.getCompanyValue());
        dealer.setRegisterDate(dto.getRegisterDate());
        dealer.setInformation(dto.getInformation()); // This is fine as is since it can be null

        if (dto.getCurrentAccount() != null) {
            CurrentAccount account = new CurrentAccount();
            account.setAccountId(dto.getCurrentAccount().getAccountId());
            // Map other account fields if needed
            dealer.setCurrentAccount(account);
        }
        return dealer;
    }

    public DealerDTO toDTO(Dealer dealer) {
        DealerDTO dto = new DealerDTO();
        dto.setId(dealer.getId());
        dto.setBusinessName(dealer.getBusinessName());
        dto.setAddress(dealer.getAddress());
        dto.setPhone(dealer.getPhone());
        dto.setEmail(dealer.getEmail());
        dto.setCompanyValue(dealer.getCompanyValue());
        dto.setRegisterDate(dealer.getRegisterDate());
        dto.setInformation(dealer.getInformation()); // This is fine as is

        if (dealer.getStatusforDelar() != null) {
            dto.setStatus(dealer.getStatusforDelar().toString());
        }

        if (dealer.getCurrentAccount() != null) {
            CurrentAccountDTO accountDTO = new CurrentAccountDTO();
            accountDTO.setAccountId(dealer.getCurrentAccount().getAccountId());
            accountDTO.setTotalBalance(dealer.getCurrentAccount().getTotalBalence());
            // Map other account fields if needed
            dto.setCurrentAccount(accountDTO);
        }
        return dto;
    }
}