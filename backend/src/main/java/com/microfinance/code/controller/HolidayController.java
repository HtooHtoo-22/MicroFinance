//package com.microfinance.code.controller;
//
//import com.microfinance.code.model.Holiday;
//import com.microfinance.code.service.HolidayService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/holidays")
//public class HolidayController {
//
//    private final HolidayService holidayService;
//
//    public HolidayController(HolidayService holidayService) {
//        this.holidayService = holidayService;
//    }
//
//    @GetMapping("/myanmar")
//    public List<Holiday> getMyanmarHolidays() {
//        return holidayService.getMyanmarHoliday();
//    }
//}