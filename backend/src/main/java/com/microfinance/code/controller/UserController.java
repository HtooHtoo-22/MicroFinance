package com.microfinance.code.controller;

import com.microfinance.code.dto.UserDTO;
import com.microfinance.code.dto.UserResponseDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody UserDTO dto) {
        try {
            UserResponseDTO createdUser = userService.createUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(HttpStatus.CREATED, 201, "User created successfully", createdUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, 400, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer id, @RequestBody UserDTO dto) {
        System.out.println("Updating user with ID: " + id);
        System.out.println("UserDTO: " + dto.toString());
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, dto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok()
                .body(ApiResponse.success(HttpStatus.OK, 200, "Users retrieved successfully", users));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Integer id) {
        try {
            ApiResponse<String> response = userService.deleteUser(id);
            return ResponseEntity.status(response.getHttpStatus()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Failed to delete user"));
        }
    }
}