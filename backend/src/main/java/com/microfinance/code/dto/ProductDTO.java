package com.microfinance.code.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductDTO {

    private Integer id;
    private String productName;
    private BigDecimal value;
    private String photo;
    private Integer dealerRegisterId;
    private Boolean status; // Changed from Byte to Boolean

    // Getters and Setters
}
