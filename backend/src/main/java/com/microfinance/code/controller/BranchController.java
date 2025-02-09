package com.microfinance.code.controller;

import com.microfinance.code.service.interFace.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BranchController {
    @Autowired
    private BranchService branchService;
}
