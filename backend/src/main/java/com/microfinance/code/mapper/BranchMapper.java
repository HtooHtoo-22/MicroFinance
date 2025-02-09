package com.microfinance.code.mapper;

import com.microfinance.code.dto.BranchDTO;
import com.microfinance.code.model.Branch;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BranchMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BranchDTO toDTO(Branch branch) {
        if (branch == null) {
            return null;
        }
        BranchDTO dto = new BranchDTO();
        dto.setId(branch.getId());
        dto.setCode(branch.getCode());
        dto.setName(branch.getName());
        dto.setCreatedDate(branch.getCreatedDate() != null ? branch.getCreatedDate().format(formatter) : null);
        dto.setAddress(branch.getAddress());
        dto.setState(branch.getState());
        dto.setTownship(branch.getTownship());
        dto.setStatus(branch.getStatus().getDisplayName()); // Convert Enum to String
        return dto;
    }
    public Branch toEntity(BranchDTO dto) {
        if (dto == null) {
            return null;
        }

        Branch branch = new Branch();
        branch.setId(dto.getId());
        branch.setCode(dto.getCode());
        branch.setName(dto.getName());
        branch.setAddress(dto.getAddress());
        branch.setState(dto.getState());
        branch.setTownship(dto.getTownship());
        return branch;
    }
}
