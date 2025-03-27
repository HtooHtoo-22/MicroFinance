package com.microfinance.code.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CollateralDTO {
    private Integer id;
    private BigDecimal value;
    private String description;
    private String address;
    private String image;
    private MultipartFile imageFile;
    private Integer collateralTypeId; // Using Integer instead of object reference
    private Integer currentAccountId;
    private String collateralTypeName;
    private String cifId;
    private String ownerName;
    private BigDecimal remainingValue;
    private List<String> smeLoanIds;
    private String name;
    private BigDecimal usedValue;
    private List<Integer> smeLoanPrimaryIds;

}
