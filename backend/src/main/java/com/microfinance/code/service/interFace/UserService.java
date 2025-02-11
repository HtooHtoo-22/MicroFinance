package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.BranchDTO;
import com.microfinance.code.dto.UserDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.model.Branch;
import com.microfinance.code.model.User;

import java.util.List;

public interface UserService {
    public void hello();
    User createUser(UserDTO dto);
    User updateUser(Integer id, UserDTO dto);
    ApiResponse<String> deleteUser(Integer id);
    User getUserById(Integer id);
    List<UserDTO> getAllUser();
}
