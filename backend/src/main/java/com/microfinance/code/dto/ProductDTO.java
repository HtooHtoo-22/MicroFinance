package com.microfinance.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductDTO {

    private Integer id;
    private String productName;
    private BigDecimal value;
    private String photo;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // Client cannot set this
    private Integer dealerId;
    private Boolean status; // Changed from Byte to Boolean

}
