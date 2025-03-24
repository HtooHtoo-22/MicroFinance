package com.microfinance.code.mapper;

import com.microfinance.code.dto.PermissionDTO;
import com.microfinance.code.model.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionDTO toDTO(Permission permission) {
        if (permission == null) {
            return null;
        }

        return PermissionDTO.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .active(permission.isActive())
                .build();
    }

    public Permission toEntity(PermissionDTO permissionDTO) {
        if (permissionDTO == null) {
            return null;
        }

        return Permission.builder()
                .id(permissionDTO.getId())
                .permissionName(permissionDTO.getPermissionName())
                .description(permissionDTO.getDescription())
                .active(permissionDTO.isActive())
                .build();
    }
}