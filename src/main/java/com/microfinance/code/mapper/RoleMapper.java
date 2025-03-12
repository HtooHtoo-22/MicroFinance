package com.microfinance.code.mapper;

import org.springframework.stereotype.Component;
import com.microfinance.code.dto.RoleDTO;
import com.microfinance.code.model.Role;

@Component
public class RoleMapper {

    public RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleDescription(role.getRoleDescription());
        dto.setActive(role.isActive());
        return dto;
    }

    public Role toEntity(RoleDTO dto) {
        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setRoleDescription(dto.getRoleDescription());
        role.setActive(dto.isActive());
        return role;
    }
}
