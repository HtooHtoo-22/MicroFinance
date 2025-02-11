package com.microfinance.code.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String name;
    private String email;
    private String password;
    private boolean active;
    private int brandId;

}
