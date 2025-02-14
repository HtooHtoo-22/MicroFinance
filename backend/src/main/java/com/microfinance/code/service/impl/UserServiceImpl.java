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
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Branch branch = branchRepo.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Branch Not Found"));
        Role role = roleRepo.findById(dto.getRoleID())
                .orElseThrow(() -> new NotFoundException("Role Not Found"));

        String newUserId = generateNextUserId();
        User user = userMapper.toEntity(dto, branch, role, newUserId);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepo.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    private String generateNextUserId() {
        String lastUserId = userRepo.findLastUserId();
        if (lastUserId == null) {
            return "001";
        }
        int lastIdNumber = Integer.parseInt(lastUserId);
        int nextIdNumber = lastIdNumber + 1;
        return String.format("%03d", nextIdNumber);
    }

    @Override
    public UserResponseDTO updateUser(Integer id, UserDTO dto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updatedUser = userRepo.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public ApiResponse<String> deleteUser(Integer id) {
        if (!userRepo.existsById(id)) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "User not found");
        }
        userRepo.deleteById(id);
        return ApiResponse.success(HttpStatus.OK, 200, "User deleted successfully", "Deleted");
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
}