package com.microfinance.code.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);
        role = roleRepository.save(role);
        return roleMapper.toDTO(role);
    }

    @Override
    public RoleDTO updateRole(Integer id, RoleDTO roleDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with ID: " + id));

        role.setRoleName(roleDTO.getRoleName());
        role.setRoleDescription(roleDTO.getRoleDescription());
        role.setActive(roleDTO.isActive());

        role = roleRepository.save(role);
        return roleMapper.toDTO(role);
    }

    @Override
    public void deleteRole(Integer id) {
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException("Role not found with ID: " + id);
        }
        roleRepository.deleteById(id);
    }
}
