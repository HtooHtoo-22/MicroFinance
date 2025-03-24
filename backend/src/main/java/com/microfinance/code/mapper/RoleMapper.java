package com.microfinance.code.mapper;

import com.microfinance.code.dto.RoleDTO;
import com.microfinance.code.model.Permission;
import com.microfinance.code.model.Role;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleDescription(role.getRoleDescription());
        dto.setActive(role.isActive());
        dto.setPermissions(role.getPermissions().stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet()));
        return dto;
    }

    public Role toEntity(RoleDTO dto) {
        Role role = new Role();
        updateEntity(dto, role);
        return role;
    }

    public void updateEntity(RoleDTO dto, Role entity) {
        entity.setRoleName(dto.getRoleName());
        entity.setRoleDescription(dto.getRoleDescription());
        entity.setActive(dto.isActive());
        // Permissions handled separately in service
    }
}