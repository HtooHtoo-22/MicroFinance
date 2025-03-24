package com.microfinance.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RoleDTO {
    private Integer id;
    private String roleName;
    private String roleDescription;
    private boolean active;
    private Set<String> permissions; // List of permission names (e.g., "READ_USERS", "CREATE_LOANS")
}