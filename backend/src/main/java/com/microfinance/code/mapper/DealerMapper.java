package com.microfinance.code.mapper;

import com.microfinance.code.dto.DealerDTO;
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
//        dealer.setPassword(dto.getPassword());

//        CurrentAccount account = new CurrentAccount();
//        account.setId(Integer.valueOf(dto.getCurrentAccountId()));
//        dealer.setCurrentAccount(account);

        return dealer;
    }

    public DealerDTO toDTO(Dealer dealer) {
        DealerDTO dto = new DealerDTO();
        dto.setId(dealer.getId());
        dto.setBusinessName(dealer.getBusinessName());
        dto.setAddress(dealer.getAddress());
        dto.setPhone(dealer.getPhone());
        dto.setEmail(dealer.getEmail());
        dto.setCurrentAccountId(dealer.getCurrentAccount().getAccountId()); // Use accountId string
        dto.setCompanyValue(dealer.getCompanyValue());
        dto.setRegisterDate(dealer.getRegisterDate());
        dto.setStatus(dealer.getStatusforDelar().toString());
        return dto;
    }
}
