package com.microfinance.code.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDTO {
    private Integer id;
    private String userId;
    private String name;
    private String email;
    private String password;
    private String branchName;
    private String roleName;
    private String createDate;
}