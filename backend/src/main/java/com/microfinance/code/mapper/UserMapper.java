package com.microfinance.code.mapper;

import com.microfinance.code.dto.UserDTO;
import com.microfinance.code.model.Branch;
import com.microfinance.code.model.Role;
import com.microfinance.code.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(null); // Avoid exposing password
        dto.setActive(user.getRole() != null); // Assuming active means having a role
        return dto;
    }

    public User toEntity(UserDTO dto, Branch branch, Role role) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setUserId(UUID.randomUUID().toString()); // Generate unique userId
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Hash before saving
        user.setBranch(branch);
        user.setRole(role);
        user.setRegisteredDate(LocalDateTime.now());
        return user;
    }


}
