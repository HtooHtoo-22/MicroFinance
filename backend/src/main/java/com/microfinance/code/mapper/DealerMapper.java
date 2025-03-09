package com.microfinance.code.mapper;

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

//        CurrentAccount account = new CurrentAccount();
//        account.setId(Integer.valueOf(dto.getCurrentAccountId()));
//        dealer.setCurrentAccount(account);
        if (dto.getCurrentAccountId() != null) {
            CurrentAccount account = new CurrentAccount();
            account.setAccountId(dto.getCurrentAccountId()); // Use accountId string
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

        if (dealer.getStatusforDelar() != null) {
            dto.setStatus(dealer.getStatusforDelar().toString()); // Map enum to string
        }

        if (dealer.getCurrentAccount() != null) {
            dto.setCurrentAccountId(dealer.getCurrentAccount().getAccountId()); // Use accountId string
        }
        return dto;
    }
}
