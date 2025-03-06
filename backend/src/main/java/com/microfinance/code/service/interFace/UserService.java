package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.UserDTO;
import com.microfinance.code.dto.UserResponseDTO;
import com.microfinance.code.etc.ApiResponse;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserDTO dto);
    UserResponseDTO updateUser(Integer id, UserDTO dto);
    ApiResponse<String> deleteUser(Integer id);
    UserResponseDTO getUserById(Integer id);
    List<UserResponseDTO> getAllUsers();

    Long getActiveUserCountByBranch(Integer branchId);
}