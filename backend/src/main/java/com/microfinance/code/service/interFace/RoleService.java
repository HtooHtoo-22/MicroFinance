package com.microfinance.code.service.interFace;

import java.util.List;
import com.microfinance.code.dto.RoleDTO;

public interface RoleService {
    List<RoleDTO> getAllRoles();
    RoleDTO getRoleById(Integer id);
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO updateRole(Integer id, RoleDTO roleDTO);
    void deleteRole(Integer id);
}
