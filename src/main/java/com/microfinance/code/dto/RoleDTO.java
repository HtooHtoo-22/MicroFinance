package com.microfinance.code.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {
    private int id;
    private String roleName;
    private String roleDescription;
    private boolean active;
}
