package com.microfinance.code.dto;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Getter
@Setter
public class DealerDTO {
    private Integer id;
    private String businessName;
    private String address;
    private String phone;
    private String email;
    private LocalDate registerDate;
    private String status;

    private CurrentAccountDTO currentAccount;
    private Double companyValue;
    private String information;
}
