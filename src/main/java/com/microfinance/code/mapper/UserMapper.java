package com.microfinance.code.mapper;

import com.microfinance.code.dto.UserDTO;
import com.microfinance.code.dto.UserResponseDTO;
import com.microfinance.code.model.Branch;
import com.microfinance.code.model.Role;
import com.microfinance.code.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDTO dto, Branch branch, Role role, String userId) {
        return User.builder()
                .userId(userId)
                .name(dto.getName())
                .branch(branch)
                .role(role)
                .active(dto.isActive())
                .build();
    }

    public UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .branchName(user.getBranch().getName())
                .roleName(user.getRole().getRoleName())
                .createDate(user.getCreateDate().toString())

                .build();
    }
}