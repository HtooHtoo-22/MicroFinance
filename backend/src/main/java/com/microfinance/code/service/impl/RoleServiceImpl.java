package com.microfinance.code.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microfinance.code.dto.RoleDTO;
import com.microfinance.code.mapper.RoleMapper;
import com.microfinance.code.model.Role;
import com.microfinance.code.repository.RoleRepository;
import com.microfinance.code.service.interFace.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(RoleMapper::toDTO).toList();
    }

    @Override
    public RoleDTO getRoleById(Integer id) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        return roleOpt.map(RoleMapper::toDTO).orElse(null);
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = RoleMapper.toEntity(roleDTO);
        role = roleRepository.save(role);
        return RoleMapper.toDTO(role);
    }

    @Override
    public RoleDTO updateRole(Integer id, RoleDTO roleDTO) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            role.setRoleName(roleDTO.getRoleName());
            role.setRoleDescription(roleDTO.getRoleDescription());
            role.setActive(roleDTO.isActive());
            role = roleRepository.save(role);
            return RoleMapper.toDTO(role);
        }
        return null;
    }

    @Override
    public void deleteRole(Integer id) {
        roleRepository.deleteById(id);
    }
}
