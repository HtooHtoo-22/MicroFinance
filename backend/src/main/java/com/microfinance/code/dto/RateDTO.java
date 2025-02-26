package com.microfinance.code.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateDTO {
    private Integer id;
    private String rateType;
    private Double value;
    private boolean status;
}
