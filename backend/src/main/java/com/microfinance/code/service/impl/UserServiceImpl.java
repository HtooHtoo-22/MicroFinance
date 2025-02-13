package com.microfinance.code.service.impl;

import com.microfinance.code.dto.UserDTO;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final BranchRepo branchRepo;
    private final RoleRepository roleRepo;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(
            UserRepo userRepo,
            BranchRepo branchRepo,
            RoleRepository roleRepository,
            UserMapper userMapper,
            BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.branchRepo = branchRepo;
        this.roleRepo = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void hello() {

    }
    @Override
    public User createUser(UserDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Branch branch = branchRepo.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand Not Found"));
        Role role = roleRepo.findById(dto.getRoleID())
                .orElseThrow(() -> new NotFoundException("Role Not Found"));

        String newUserId = generateNextUserId();
        User user = userMapper.toEntity(dto, branch, role, newUserId);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepo.save(user);
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
    public User updateUser(Integer id, UserDTO dto) {
        Optional<User> existingUserOpt = userRepo.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = existingUserOpt.get();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword())); // Hash password
        }

        return userRepo.save(user);
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
    public User getUserById(Integer id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<UserDTO> getAllUser() {
        List<User> users = userRepo.findAll();
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }
}
