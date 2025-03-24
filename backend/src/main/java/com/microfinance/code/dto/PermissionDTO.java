package com.microfinance.code.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    private Integer id;
    private String permissionName;
    private String description;
    private boolean active;
}