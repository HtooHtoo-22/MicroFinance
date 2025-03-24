package com.microfinance.code.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.microfinance.code.model.Permission;
import com.microfinance.code.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microfinance.code.dto.RoleDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.RoleMapper;
import com.microfinance.code.model.Role;
import com.microfinance.code.repository.RoleRepository;
import com.microfinance.code.service.interFace.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;  // Now correctly using an instance

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO getRoleById(Integer id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Role not found with ID: " + id));
    }

    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);
        setPermissionsFromNames(role, roleDTO.getPermissions());
        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleDTO updateRole(Integer id, RoleDTO roleDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        roleMapper.updateEntity(roleDTO, role);
        setPermissionsFromNames(role, roleDTO.getPermissions());

        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Override
    public void deleteRole(Integer id) {
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException("Role not found with ID: " + id);
        }
        roleRepository.deleteById(id);
    }

    private void setPermissionsFromNames(Role role, Set<String> permissionNames) {
        Set<Permission> permissions = permissionNames.stream()
                .map(name -> permissionRepository.findByPermissionName(name)
                        .orElseThrow(() -> new NotFoundException("Permission not found: " + name)))
                .collect(Collectors.toSet());
        role.setPermissions(permissions);
    }
}
