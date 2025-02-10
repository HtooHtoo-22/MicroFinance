package com.microfinance.code.service.impl;

import com.microfinance.code.mapper.BranchMapper;
import com.microfinance.code.repository.BranchRepo;
import com.microfinance.code.service.interFace.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private BranchRepo branchRepo;
    @Override
    public void hello() {
        System.out.println("Hello");
    }
}
