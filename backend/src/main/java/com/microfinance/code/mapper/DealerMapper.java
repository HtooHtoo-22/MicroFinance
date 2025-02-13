package com.microfinance.code.mapper;

import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.status.DealerStatus;
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
        dealer.setPassword(dto.getPassword());

        CurrentAccount account = new CurrentAccount();
        account.setId(dto.getCurrentAccountId());
        dealer.setCurrentAccount(account);

        return dealer;
    }

    public DealerDTO toDTO(Dealer dealer) {
        DealerDTO dto = new DealerDTO();
        dto.setId(dealer.getId());
        dto.setBusinessName(dealer.getBusinessName());
        dto.setAddress(dealer.getAddress());
        dto.setPhone(dealer.getPhone());
        dto.setEmail(dealer.getEmail());
        dto.setStatus(dealer.getStatus().toString());
        dto.setCurrentAccountId(dealer.getCurrentAccount().getId());
        return dto;
    }
}
