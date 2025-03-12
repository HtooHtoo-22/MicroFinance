package com.microfinance.code.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BranchDTO {
    private Integer id;
    private String code;
    private String name;
    private String createdDate;
    private String address;
    private String state;
    private String township;
    private String status;
}
