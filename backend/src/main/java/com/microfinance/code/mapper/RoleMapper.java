package com.microfinance.code.mapper;

import com.microfinance.code.dto.RoleDTO;
import com.microfinance.code.model.Role;

public class RoleMapper {

    public static RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleDescription(role.getRoleDescription());
        dto.setActive(role.isActive());
        return dto;
    }

    public static Role toEntity(RoleDTO dto) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setRoleName(dto.getRoleName());
        role.setRoleDescription(dto.getRoleDescription());
        role.setActive(dto.isActive());
        return role;
    }
}
