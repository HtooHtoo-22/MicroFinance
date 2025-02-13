package com.microfinance.code.controller;

import com.microfinance.code.dto.UserDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.model.User;
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
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserDTO dto) {
        try {
            User createdUser = userService.createUser(dto);
            UserDTO createdUserDTO = new UserDTO();
            createdUserDTO.setName(createdUser.getName());
            createdUserDTO.setEmail(createdUser.getEmail());
            createdUserDTO.setPassword("********");
            createdUserDTO.setActive(true);
            createdUserDTO.setBrandId(createdUser.getBranch() != null ? createdUser.getBranch().getId() : 0);
            createdUserDTO.setRoleID(createdUser.getRole() != null ? createdUser.getRole().getId() : 0);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(HttpStatus.CREATED, 201, "User created successfully", createdUserDTO));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, 400, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserDTO dto) {
        try {
            User updatedUser = userService.updateUser(id, dto);
            UserDTO updatedUserDTO = new UserDTO();
            updatedUserDTO.setName(updatedUser.getName());
            updatedUserDTO.setEmail(updatedUser.getEmail());

            return ResponseEntity.ok(updatedUserDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Integer id) {
        ApiResponse<String> response = userService.deleteUser(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);    }
}

