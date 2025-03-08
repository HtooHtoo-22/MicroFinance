package com.microfinance.code.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.microfinance.code.dto.RoleDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ApiResponse.success(HttpStatus.OK, 200, "Roles retrieved successfully", roles);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleDTO> getRoleById(@PathVariable Integer id) {
        RoleDTO role = roleService.getRoleById(id);
        if (role != null) {
            return ApiResponse.success(HttpStatus.OK, 200, "Role retrieved successfully", role);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "Role not found");
        }
    }

    @PostMapping("/create")
    public ApiResponse<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        RoleDTO createdRole = roleService.createRole(roleDTO);
        return ApiResponse.success(HttpStatus.CREATED, 201, "Role created successfully", createdRole);
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleDTO> updateRole(@PathVariable Integer id, @RequestBody RoleDTO roleDTO) {
        RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
        if (updatedRole != null) {
            return ApiResponse.success(HttpStatus.OK, 200, "Role updated successfully", updatedRole);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "Role not found");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ApiResponse.success(HttpStatus.NO_CONTENT, 204, "Role deleted successfully");
    }
}
