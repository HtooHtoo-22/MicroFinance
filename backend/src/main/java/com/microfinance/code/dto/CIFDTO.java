package com.microfinance.code.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CIFDTO {
    private Integer id;
    private String cifId;
    private String userName;
    private String gender;
    private String job;
    private Double incomeAmount;
    private String nrc;
    private String frontNRCUrl;
    private String backNRCUrl;
    private String userPhotoURL;
    private String phone;
    private String email;
    private String createdDate;
    private String state;
    private String township;
    private String address;
    private String status;
    private Integer branchId;
    private Integer userId;
}
