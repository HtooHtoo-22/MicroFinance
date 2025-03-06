package com.microfinance.code.mapper;

import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.CIF;
import com.microfinance.code.repository.BranchRepo;
import com.microfinance.code.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CIFMapper {

    @Autowired
    private BranchRepo branchRepo;

    @Autowired
    private UserRepo userRepo;
    public CIFDTO toDTO(CIF cif) {
        if (cif == null) {
            return null;
        }

        CIFDTO dto = new CIFDTO();
        dto.setId(cif.getId());
        dto.setCifId(cif.getCifId());
        dto.setUserName(cif.getUserName());
        dto.setGender(cif.getGender());
        dto.setJob(cif.getJob());
        dto.setIncomeAmount(cif.getIncomeAmount());
        dto.setNrc(cif.getNRC());
        dto.setFrontNRCUrl(cif.getFrontNRCUrl());
        dto.setBackNRCUrl(cif.getBackNRCUrl());
        dto.setUserPhotoURL(cif.getUserPhotoURL());
        dto.setPhone(cif.getPhone());
        dto.setEmail(cif.getEmail());
        dto.setCreatedDate(cif.getCreatedDate().toString());
        dto.setState(cif.getState());
        dto.setTownship(cif.getTownship());
        dto.setAddress(cif.getAddress());
        dto.setStatus(cif.getStatus().getDisplayName());

        if (cif.getBranch() != null) {
            dto.setBranchName(cif.getBranch().getName());
        }

        // Add User Full Name
        if (cif.getUser() != null) {
            dto.setUserFullName(cif.getUser().getName());
        }


        return dto;
    }
    public CIF toEntity(CIFDTO dto) {
        if (dto == null) {
            return null;
        }
        CIF cif = new CIF();
        cif.setId(dto.getId());
        cif.setCifId(dto.getCifId());
        cif.setUserName(dto.getUserName());
        cif.setGender(dto.getGender());
        cif.setJob(dto.getJob());
        cif.setIncomeAmount(dto.getIncomeAmount());
        cif.setNRC(dto.getNrc());
        cif.setFrontNRCUrl(dto.getFrontNRCUrl());
        cif.setBackNRCUrl(dto.getBackNRCUrl());
        cif.setUserPhotoURL(dto.getUserPhotoURL());
        cif.setPhone(dto.getPhone());
        cif.setEmail(dto.getEmail());
        cif.setState(dto.getState());
        cif.setTownship(dto.getTownship());
        cif.setAddress(dto.getAddress());
        return cif;
    }
}
