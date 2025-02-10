package com.microfinance.code.controller;

import com.microfinance.code.service.interFace.CIFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CIFController {

    @Autowired
    private CIFService cifService;
}
