package com.microfinance.code.service.impl;

import com.microfinance.code.dto.UserDTO;
import com.microfinance.code.dto.UserResponseDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.UserMapper;
import com.microfinance.code.model.Branch;
import com.microfinance.code.model.Role;
import com.microfinance.code.model.User;
import com.microfinance.code.repository.BranchRepo;
import com.microfinance.code.repository.RoleRepository;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.interFace.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BranchRepo branchRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO createUser(UserDTO dto) {

        System.out.println("Searching for branch with ID: " + dto.getBranchId());

        Branch branch = branchRepo.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch Not Found"));
        System.out.println("branch" + branch.getName());

        System.out.println("Searching for role with ID: " + dto.getRoleId());
        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role Not Found"));

        String generatedEmail = generateEmail(dto.getName());
        String generatedPassword = generatePassword(role.getRoleName());
        String UserId = generateUserId(branch.getCode());
        User user = userMapper.toEntity(dto, branch, role, UserId);

        user.setEmail(generatedEmail);

        if (generatedPassword == null || generatedPassword.isEmpty()) {
            throw new RuntimeException("Generated password is invalid");
        }
        user.setPassword(passwordEncoder.encode(generatedPassword));
        System.out.println("Encoded Password: " + user.getPassword());
        User savedUser = userRepo.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    private String generateUserId(String branchcode) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Last 5 digits of timestamp
        return "UC-" + branchcode + "-"  + timestamp;
    }

    private String generateEmail(String name) {
        return name.toLowerCase().replace(" ", "") + "@richcoin.com";
    }

    private String generatePassword(String roleName) {
        switch (roleName.toUpperCase()) {
            case "ADMIN":
                return "admin@richcoin"; // Password for ADMIN role
            case "MANAGER":
                return "manager@richcoin"; // Password for MANAGER role
            case "OPERATION":
                return "operation@richcoin"; // Password for OPERATION role
            case "ENTRY":
                return "entry@richcoin"; // Password for ENTRY role
            default:
                return "default@richcoin"; // Password for unknown or unhandled roles
        }
    }


    @Override
    public UserResponseDTO updateUser(Integer id, UserDTO dto) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields
        existingUser.setName(dto.getName());
        existingUser.setBranch(branchRepo.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found")));
        existingUser.setRole(roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found")));

        User updatedUser = userRepo.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public ApiResponse<String> deleteUser(Integer id) {
        try {
            userRepo.deleteById(id);
            return ApiResponse.success(HttpStatus.OK, 200, "User deleted successfully", null);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Failed to delete user");
        }
    }

    @Override
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Long getActiveUserCountByBranch(Integer branchId) {
        return userRepo.countActiveUsersByBranch(branchId);
    }
}