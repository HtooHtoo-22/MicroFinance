package com.microfinance.code.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String name;
    private boolean active;
    private int branchId;
    private int roleId;

}