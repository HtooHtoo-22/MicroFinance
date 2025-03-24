package com.microfinance.code.service.impl;

import com.microfinance.code.dto.PermissionDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.PermissionMapper;
import com.microfinance.code.model.Permission;
import com.microfinance.code.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        Permission permission = permissionMapper.toEntity(permissionDTO);
        permission = permissionRepository.save(permission);
        return permissionMapper.toDTO(permission);
    }

    public PermissionDTO getPermissionById(Integer id) {
        return permissionRepository.findById(id)
                .map(permissionMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Permission not found with ID: " + id));
    }
}