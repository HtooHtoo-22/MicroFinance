package com.microfinance.code.controller;

import com.microfinance.code.dto.PermissionDTO;
import com.microfinance.code.service.impl.PermissionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionServiceImpl permissionService;

    @GetMapping("/list")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @PostMapping
    public ResponseEntity<PermissionDTO> createPermission(@RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(permissionService.createPermission(permissionDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Integer id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }
}
